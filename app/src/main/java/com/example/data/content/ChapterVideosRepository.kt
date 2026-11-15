package com.example.data.content

import com.example.data.model.ChapterVideo
import java.net.URLEncoder

object ChapterVideosRepository {

    val foundationPlaylists: List<ChapterVideo> = listOf(
        ChapterVideo("pl_jee_chem1", "JEE", "Chemistry", "Foundation Part 1", "Foundation Part 1 — Mole Concept to Bonding", "Arsath Curated Playlist", 4.9, "18 hrs", "https://www.youtube.com/playlist?list=PLJrOWEnx4zCiDqNydcM2PkibmUYhisEP4", "Must-watch core chemistry"),
        ChapterVideo("pl_jee_chem2", "JEE", "Chemistry", "Foundation Part 2", "Foundation Part 2 — Organic & Physical Chem", "Arsath Curated Playlist", 4.9, "22 hrs", "https://www.youtube.com/playlist?list=PLJrOWEnx4zCgpZ6FaTLoAOCm1Qv2bJUk", "High-yield JEE Organic/Physical"),
        ChapterVideo("pl_jee_phy1", "JEE", "Physics", "Foundation Part 1", "Foundation Part 1 — Mechanics & Thermodynamics", "Arsath Curated Playlist", 4.9, "24 hrs", "https://www.youtube.com/playlist?list=PLJrOWEnx4zChtu0b6jGq0sc0EAs0mZia", "35% JEE Mechanics base"),
        ChapterVideo("pl_jee_phy2", "JEE", "Physics", "Foundation Part 2", "Foundation Part 2 — Electrostatics to Modern Physics", "Arsath Curated Playlist", 4.9, "26 hrs", "https://www.youtube.com/playlist?list=PLJrOWEnx4zCiP-AIK8P3LdJewblvfweV", "60% Class 12 JEE Physics"),
        ChapterVideo("pl_jee_mat1", "JEE", "Mathematics", "Foundation Part 1", "Foundation Part 1 — Algebra, Trig, Coordinate Geom", "Arsath Curated Playlist", 4.9, "25 hrs", "https://www.youtube.com/playlist?list=PLJrOWEnx4zCjQ_EUrKnzbv17ZvISaZgQ1", "Scoring Algebra & Conics"),
        ChapterVideo("pl_jee_mat2", "JEE", "Mathematics", "Foundation Part 2", "Foundation Part 2 — Calculus, Vectors, Probability", "Arsath Curated Playlist", 5.0, "28 hrs", "https://www.youtube.com/playlist?list=PLJrOWEnx4zCgNkF3yshX_31ckag0aH4yL", "Highest-weightage Calculus"),
        ChapterVideo("pl_neet_chem1", "NEET", "Chemistry", "Foundation Part 1", "Foundation Part 1 — Mole Concept to Bonding", "Arsath Curated Playlist", 4.9, "18 hrs", "https://www.youtube.com/playlist?list=PLJrOWEnx4zCiDqNydcM2PkibmUYhisEP4", "NCERT Physical & Inorganic"),
        ChapterVideo("pl_neet_chem2", "NEET", "Chemistry", "Foundation Part 2", "Foundation Part 2 — Organic & Physical Chem", "Arsath Curated Playlist", 4.9, "22 hrs", "https://www.youtube.com/playlist?list=PLJrOWEnx4zCgpZ6FaTLoAOCm1Qv2bJUk", "Named Reactions & NCERT"),
        ChapterVideo("pl_neet_phy1", "NEET", "Physics", "Foundation Part 1", "Foundation Part 1 — Mechanics", "Arsath Curated Playlist", 4.9, "20 hrs", "https://www.youtube.com/playlist?list=PLJrOWEnx4zChtu0b6jGq0sc0EAs0mZia", "Fast NEET numerical tricks"),
        ChapterVideo("pl_neet_phy2", "NEET", "Physics", "Foundation Part 2", "Foundation Part 2 — Electrostatics to Modern Physics", "Arsath Curated Playlist", 4.9, "22 hrs", "https://www.youtube.com/playlist?list=PLJrOWEnx4zCiP-AIK8P3LdJewblvfweV", "Direct formula scorers"),
        ChapterVideo("pl_neet_bio", "NEET", "Biology", "Full NCERT Biology", "Foundation Videos — Full NCERT Biology", "Arsath Curated Playlist", 5.0, "40 hrs", "https://www.youtube.com/playlist?list=PLJrOWEnx4zCir8FWOoUJS33jGILlrgUS-", "340+/360 NCERT Line-by-Line")
    )

    private fun ytSearchUrl(query: String): String {
        val encoded = URLEncoder.encode(query, "UTF-8")
        return "https://www.youtube.com/results?search_query=$encoded"
    }

    private fun buildChapterVideosForSubject(
        exam: String,
        subject: String,
        chapters: List<String>,
        basePlaylistUrl: String
    ): List<ChapterVideo> {
        val educators = when (subject) {
            "Physics" -> listOf("Physics Wallah · Alakh Sir", "Vedantu JEE/NEET", "Unacademy Atoms", "Competishun · ABJ Sir")
            "Chemistry" -> listOf("Pankaj Sir Chemistry", "Sakshi Vora · JEE/NEET", "Mohit Tyagi · ALK Sir", "NCERT Chemistry Pro")
            "Mathematics" -> listOf("Neha Agrawal Mathematically Inclined", "NV Sir · Unacademy", "Mohit Tyagi JEE Maths", "Arvind Kalia Sir")
            else -> listOf("Tarun Sir Botany", "Garima Goel Biology", "Ritu Rattewal NCERT", "Seep Pahuja Unacademy")
        }
        return chapters.flatMapIndexed { idx, chapter ->
            val rating1 = 4.8 + ((idx % 3) * 0.1).coerceAtMost(0.2)
            val rating2 = 4.7 + (((idx + 1) % 3) * 0.1)
            val ed1 = educators[idx % educators.size]
            val ed2 = educators[(idx + 1) % educators.size]
            val weightage = when {
                idx % 4 == 0 -> "High Weightage (3-4 Qs)"
                idx % 4 == 1 -> "Core Concept Builder"
                idx % 4 == 2 -> "Frequent PYQ Topic"
                else -> "Rapid Revision Shot"
            }
            listOf(
                ChapterVideo(
                    id = "${exam}_${subject}_ch${idx + 1}_v1",
                    exam = exam,
                    subject = subject,
                    chapter = chapter,
                    title = "$chapter — Complete One-Shot & PYQs ($exam)",
                    educator = ed1,
                    rating = String.format("%.1f", rating1).toDouble(),
                    duration = "${2 + (idx % 3)}h ${(15 + idx * 7) % 55}m",
                    url = ytSearchUrl("$exam $subject $chapter one shot PYQ"),
                    weightageNote = weightage
                ),
                ChapterVideo(
                    id = "${exam}_${subject}_ch${idx + 1}_v2",
                    exam = exam,
                    subject = subject,
                    chapter = chapter,
                    title = "$chapter — Top Numericals & Foundation Playlist",
                    educator = ed2,
                    rating = String.format("%.1f", rating2).toDouble(),
                    duration = "${1 + (idx % 2)}h ${(20 + idx * 11) % 50}m",
                    url = if (idx % 2 == 0) basePlaylistUrl else ytSearchUrl("$exam $subject $chapter top problems"),
                    weightageNote = "Top Rated · ${((rating2 * 20).toInt())}% Student Approval"
                )
            )
        }
    }

    val allChapterVideos: List<ChapterVideo> by lazy {
        buildChapterVideosForSubject("JEE", "Physics", SyllabusChapters.jeePhysics, "https://www.youtube.com/playlist?list=PLJrOWEnx4zChtu0b6jGq0sc0EAs0mZia") +
            buildChapterVideosForSubject("JEE", "Chemistry", SyllabusChapters.jeeChemistry, "https://www.youtube.com/playlist?list=PLJrOWEnx4zCiDqNydcM2PkibmUYhisEP4") +
            buildChapterVideosForSubject("JEE", "Mathematics", SyllabusChapters.jeeMathematics, "https://www.youtube.com/playlist?list=PLJrOWEnx4zCjQ_EUrKnzbv17ZvISaZgQ1") +
            buildChapterVideosForSubject("NEET", "Physics", SyllabusChapters.neetPhysics, "https://www.youtube.com/playlist?list=PLJrOWEnx4zChtu0b6jGq0sc0EAs0mZia") +
            buildChapterVideosForSubject("NEET", "Chemistry", SyllabusChapters.neetChemistry, "https://www.youtube.com/playlist?list=PLJrOWEnx4zCgpZ6FaTLoAOCm1Qv2bJUk") +
            buildChapterVideosForSubject("NEET", "Biology", SyllabusChapters.neetBiology, "https://www.youtube.com/playlist?list=PLJrOWEnx4zCir8FWOoUJS33jGILlrgUS-")
    }

    fun getPlaylistsForExam(exam: String): List<ChapterVideo> =
        foundationPlaylists.filter { it.exam == exam }

    fun getVideosForExamAndSubject(exam: String, subject: String): List<ChapterVideo> =
        allChapterVideos.filter { it.exam == exam && it.subject == subject }
            .sortedByDescending { it.rating }
}
