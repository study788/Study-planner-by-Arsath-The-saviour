const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");
const serverTimestamp = () => new Date(Date.now() - 2000);

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";
const ADMIN_EMAIL = "banusait2005@gmail.com";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

function validProfile(uid, role = "student") {
  return {
    userId: uid,
    name: "Alice Kumar",
    email: "alice@example.com",
    studentClass: "Class 12",
    parentName: "Ramesh Kumar",
    contact: "9876543210",
    exam: "JEE",
    plan: "1year",
    role,
    active: true,
    sessionsCount: 0,
    joinDate: "15 Nov 2026",
    createdAt: serverTimestamp(),
    updatedAt: serverTimestamp(),
  };
}

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated caller cannot read users or custom_videos", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(unauthDb.collection("custom_videos").where("isPublic", "==", true).get());
});

test("Student can create and read own profile, cannot self-assign admin or ghost field", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID, {
    email: "alice@example.com",
    email_verified: true,
  }).firestore();

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set(validProfile(ALICE_UID))
  );
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());

  // Privilege escalation test
  await assertFails(
    aliceDb.collection("users").doc("alice_admin_try").set(validProfile("alice_admin_try", "admin"))
  );

  // Shadow field test
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      isVerified: true,
      updatedAt: serverTimestamp(),
    })
  );
});

test("Cross-user isolation: Bob cannot read Alice profile, test_attempts, or list users", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set(validProfile(ALICE_UID))
  );
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("test_attempts").doc("att_1").set({
      userId: ALICE_UID,
      exam: "JEE",
      testType: "daily",
      title: "Daily Physics Test",
      score: 4,
      total: 5,
      pct: 80,
      grade: "A",
      durationSeconds: 320,
      questions: [{ id: "q1", selected: 0, correct: 0 }],
      dateLabel: "15 Nov 2026",
      createdAt: serverTimestamp(),
      updatedAt: serverTimestamp(),
    })
  );

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
  await assertFails(bobDb.collection("users").get());
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("test_attempts").get()
  );
});

test("Email spoofing test: unverified admin email is rejected from listing users or writing videos", async () => {
  const spoofDb = testEnv.authenticatedContext("spoof_uid", {
    email: ADMIN_EMAIL,
    email_verified: false,
  }).firestore();

  await assertFails(spoofDb.collection("users").get());
  await assertFails(
    spoofDb.collection("custom_videos").doc("vid_1").set({
      isPublic: true,
      exam: "JEE",
      subject: "Physics",
      chapter: "Kinematics",
      title: "Test Video",
      url: "https://youtube.com/watch?v=123",
      rating: 4.9,
      authorUid: "spoof_uid",
      createdAt: serverTimestamp(),
      updatedAt: serverTimestamp(),
    })
  );
});

test("Verified admin can list students, publish video, and student can query public videos", async () => {
  const adminDb = testEnv.authenticatedContext("admin_uid", {
    email: ADMIN_EMAIL,
    email_verified: true,
  }).firestore();

  await assertSucceeds(adminDb.collection("users").get());
  await assertSucceeds(
    adminDb.collection("custom_videos").doc("vid_1").set({
      isPublic: true,
      exam: "JEE",
      subject: "Physics",
      chapter: "Kinematics",
      title: "Top Rated Kinematics",
      url: "https://youtube.com/watch?v=abc",
      rating: 4.9,
      authorUid: "admin_uid",
      createdAt: serverTimestamp(),
      updatedAt: serverTimestamp(),
    })
  );

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("custom_videos").where("isPublic", "==", true).get()
  );
  // Query without filter must fail
  await assertFails(aliceDb.collection("custom_videos").get());
});

test("Homework session assignedAtMillis is immutable on update", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const hwRef = aliceDb.collection("users").doc(ALICE_UID).collection("homework_sessions").doc("hw_20261115");
  await assertSucceeds(
    hwRef.set({
      userId: ALICE_UID,
      dateKey: "2026-11-15",
      exam: "NEET",
      questionIds: ["n_phy_1", "n_bio_1"],
      answers: { "0": 1 },
      completed: false,
      pdfExported: false,
      assignedAtMillis: 1763200000000,
      createdAt: serverTimestamp(),
      updatedAt: serverTimestamp(),
    })
  );

  // Valid update
  await assertSucceeds(
    hwRef.update({
      completed: true,
      updatedAt: serverTimestamp(),
    })
  );

  // Tampering assignedAtMillis to bypass 2-hour delay must fail
  await assertFails(
    hwRef.update({
      assignedAtMillis: 1000,
      updatedAt: serverTimestamp(),
    })
  );
});
