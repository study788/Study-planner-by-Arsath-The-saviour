package com.example.data.content

import com.example.data.model.FlashcardItem

object FlashcardsData {

    private val highYieldCards: List<FlashcardItem> = listOf(
        // JEE & NEET Physics
        FlashcardItem("fc_phy_1", "JEE", "Physics", "Kinematics & Projectiles", "Projectile Range vs Height", "When horizontal range R equals maximum height H (R = H), what is the launch angle?", "tan θ = 4  ⇒  θ = tan⁻¹(4) ≈ 76°", "Equate u²sin2θ/g = u²sin²θ/(2g) → 4 cosθ = sinθ."),
        FlashcardItem("fc_phy_2", "JEE", "Physics", "Rotational Motion", "Rolling Speed Down Incline", "Acceleration of a solid body rolling without slipping down incline θ:", "a = g sinθ / (1 + I / (MR²))", "Solid sphere beats disc beats hollow ring every time!"),
        FlashcardItem("fc_phy_3", "JEE", "Physics", "Electrostatics & Gauss Law", "Dipole Axial vs Equatorial", "Ratio of axial electric field to equatorial field at equal far distance r:", "E_axial : E_equatorial = 2 : 1 (opposite directions)", "Axial points along p; Equatorial points opposite to p."),
        FlashcardItem("fc_phy_4", "JEE", "Physics", "Current Electricity", "Max Power Transfer Theorem", "When does an external load R receive maximum power from a cell of internal resistance r?", "When external R = internal r; P_max = E² / (4r)", "At max power transfer, circuit efficiency is exactly 50%."),
        FlashcardItem("fc_phy_5", "JEE", "Physics", "Modern Physics & Semiconductors", "Bohr Energy & Radius Scaling", "How do radius r_n and energy E_n scale with Z and n in H-like atoms?", "r_n ∝ n² / Z  ;  E_n = −13.6 · Z² / n² eV", "Radius grows with n², energy magnitude drops with 1/n²."),
        FlashcardItem("fc_phy_6", "NEET", "Physics", "Units & Error Analysis", "Error Propagation Formula", "If Z = A² B³ / C^½, what is maximum fractional error ΔZ/Z?", "ΔZ/Z = 2(ΔA/A) + 3(ΔB/B) + ½(ΔC/C)", "Percentage errors always ADD — even for denominator powers!"),
        FlashcardItem("fc_phy_7", "NEET", "Physics", "Thermodynamics & KTG", "Mayer's & Adiabatic Relation", "State Mayer's relation and γ = C_p / C_v for monoatomic & diatomic gases.", "C_p − C_v = R; γ = 5/3 (mono), 7/5 (diatomic)", "Adiabatic work: W = nR(T₁ − T₂) / (γ − 1)."),
        FlashcardItem("fc_phy_8", "NEET", "Physics", "Ray & Wave Optics", "Lens Maker Formula", "Thin lens focal length in medium μ_m vs lens μ_l:", "1/f = (μ_l/μ_m − 1)(1/R₁ − 1/R₂)", "Dipping glass lens in higher-μ medium reverses diverging/converging behavior!"),

        // JEE & NEET Chemistry
        FlashcardItem("fc_chem_1", "JEE", "Chemistry", "Chemical Bonding & VSEPR", "Paramagnetic Diatomics (MOT)", "Why is O₂ paramagnetic while N₂ is diamagnetic?", "O₂ has 2 unpaired electrons in π*2p orbitals (Bond Order = 2).", "B₂ and O₂ are classic paramagnetic MOT stars (10 and 16 e⁻)."),
        FlashcardItem("fc_chem_2", "JEE", "Chemistry", "Chemical & Ionic Equilibrium", "Relation between Kp and Kc", "How are Kp and Kc connected for gaseous equilibria?", "Kp = Kc · (RT)^Δng", "Δng = gaseous product moles − gaseous reactant moles only!"),
        FlashcardItem("fc_chem_3", "JEE", "Chemistry", "General Organic Chemistry (GOC)", "Carbocation Stability Sequence", "Rank common carbocations from most stable to least stable:", "Tropylium > (C₆H₅)₃C⁺ > Benzyl/Allyl > 3° > 2° > 1° > CH₃⁺", "3° alkyl has 9 hyperconjugable α-H atoms."),
        FlashcardItem("fc_chem_4", "JEE", "Chemistry", "Coordination Compounds", "Spectrochemical Series Trap", "Why is [Co(NH₃)₆]³⁺ diamagnetic (d²sp³) while [CoF₆]³⁻ is paramagnetic (sp³d²)?", "NH₃ is a strong-field ligand forcing pairing; F⁻ is weak-field.", "Strong ligands CO > CN⁻ > en > NH₃ > H₂O > F⁻ > Cl⁻."),
        FlashcardItem("fc_chem_5", "NEET", "Chemistry", "Redox & Electrochemistry", "Nernst Equation at 298 K", "Write standard simplified Nernst equation at 25°C (298 K):", "E_cell = E°_cell − (0.0591 / n) log₁₀ Q", "Equilibrium: E_cell = 0 ⇒ E°_cell = (0.0591/n) log Kc."),
        FlashcardItem("fc_chem_6", "NEET", "Chemistry", "Solutions & Chemical Kinetics", "Zero vs First Order Half-Life", "Compare concentration dependence of t₁/₂ for zero and first order.", "Zero order: t₁/₂ = [A]₀/(2k); First order: t₁/₂ = 0.693/k (independent of [A]₀)", "Radioactive decay is strictly first order!"),

        // JEE Mathematics
        FlashcardItem("fc_mat_1", "JEE", "Mathematics", "Complex Numbers & Quadratics", "Cube Roots of Unity Properties", "Key identities for ω (complex cube root of 1):", "ω³ = 1, 1 + ω + ω² = 0, ω² = 1/ω = conjugate(ω)", "Vertices 1, ω, ω² form an equilateral triangle on unit circle."),
        FlashcardItem("fc_mat_2", "JEE", "Mathematics", "Indefinite & Definite Integration", "King's & Queen's Property", "State King's Property for definite integrals on [a, b]:", "∫_a^b f(x) dx = ∫_a^b f(a + b − x) dx", "Solves sinⁿ/(sinⁿ+cosⁿ) integrals in 2 steps → answer (b−a)/2."),
        FlashcardItem("fc_mat_3", "JEE", "Mathematics", "Conic Sections (Parabola, Ellipse, Hyperbola)", "Eccentricity & Latus Rectum", "Compare e and latus rectum for Parabola, Ellipse, Hyperbola:", "Parabola: e=1, 4a | Ellipse: e<1, 2b²/a | Hyperbola: e>1, 2b²/a", "For Rectangular Hyperbola x²−y²=a², e = √2."),
        FlashcardItem("fc_mat_4", "JEE", "Mathematics", "Matrices & Determinants", "System Consistency (Cramer)", "When does AX = B have infinitely many solutions vs no solution?", "Δ = 0 and all Δ_i = 0 ⇒ infinite (or parallel); Δ = 0 & any Δ_i ≠ 0 ⇒ No solution.", "Unique solution whenever det(A) ≠ 0."),

        // NEET Biology
        FlashcardItem("fc_bio_1", "NEET", "Biology", "Cell: The Unit of Life & Cell Cycle", "Meiosis I Sub-stages Mnemonic", "Sequence & highlights of Prophase I (L-Z-P-D-D):", "Leptotene (condense) → Zygotene (synapsis) → Pachytene (crossing over) → Diplotene (chiasmata) → Diakinesis (terminalisation)", "Remember: 'Lazy Zebra Pushes Donkey Down'."),
        FlashcardItem("fc_bio_2", "NEET", "Biology", "Photosynthesis & Plant Respiration", "C3 vs C4 Plant Comparison", "Key enzyme & Kranz anatomy differences in C3 vs C4 plants:", "C3 uses RuBisCO (3-PGA, photorespiration occurs); C4 uses PEPcase first (OAA, Kranz anatomy, no photorespiration).", "Maize, Sorghum, Sugarcane are classic NCERT C4 examples."),
        FlashcardItem("fc_bio_3", "NEET", "Biology", "Molecular Basis of Inheritance", "DNA Replication Enzymes", "Roles of Helicase, Primase, DNA Pol III, Ligase:", "Helicase unzips; Primase adds RNA primer; DNA Pol III extends 5'→3'; Ligase joins Okazaki fragments.", "DNA Polymerase NEVER initiates — always needs a free 3'-OH!"),
        FlashcardItem("fc_bio_4", "NEET", "Biology", "Breathing, Circulation & Body Fluids", "Oxygen Dissociation Curve Shift", "What shifts the Bohr Hb-O₂ dissociation curve to the RIGHT?", "High pCO₂, High H⁺ (low pH), High 2,3-BPG, High Temperature.", "Right shift = more O₂ unloaded to active exercising tissues!")
    )

    fun getFlashcards(exam: String, subject: String): List<FlashcardItem> {
        val exact = highYieldCards.filter { it.exam == exam && it.subject == subject }
        val chapters = SyllabusChapters.chaptersFor(exam, subject)
        val generated = chapters.mapIndexed { idx, chapter ->
            val id = "${exam}_${subject}_fc_ch${idx + 1}"
            FlashcardItem(
                id = id,
                exam = exam,
                subject = subject,
                chapter = chapter,
                frontTitle = "$chapter — Rapid Concept Card",
                frontPrompt = "What are the highest-yield $exam checkpoints & formula traps to recall in $chapter?",
                backAnswer = when (subject) {
                    "Physics" -> "Verify vector signs, conservation laws (energy/momentum/charge), limiting behavior, and dimensional balance."
                    "Chemistry" -> "Balance stoichiometry & oxidation numbers; apply temperature/pressure Le Chatelier checks and NCERT exceptions."
                    "Mathematics" -> "Check domain restrictions, boundary points, symmetry properties, and discriminant conditions."
                    else -> "Recall exact NCERT terminology, enzyme cofactors, ploidy counts (n/2n/3n), and physiological feedback loops."
                },
                memoryHook = "Chapter ${idx + 1} of ${chapters.size} · Master before Weekly Sunday Test"
            )
        }
        return exact + generated
    }
}
