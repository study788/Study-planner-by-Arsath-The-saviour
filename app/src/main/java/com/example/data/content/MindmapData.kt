package com.example.data.content

import com.example.data.model.ChapterMindmap
import com.example.data.model.MindmapBranch

object MindmapData {

    private val subjectPalettes = listOf(
        0xFF2563EB, // Blue
        0xFF10B981, // Emerald
        0xFFF59E0B, // Amber
        0xFF8B5CF6  // Violet
    )

    private val curatedSummaries: Map<String, List<MindmapBranch>> = mapOf(
        "Kinematics" to listOf(
            MindmapBranch("Equations of Motion", 0xFF2563EB, listOf("v = u + at", "s = ut + ½at²", "v² = u² + 2as", "s_n = u + a(n − ½)")),
            MindmapBranch("Graphs & Calculus", 0xFF10B981, listOf("Slope of x-t = velocity", "Slope of v-t = acceleration", "Area under v-t = displacement", "a = v dv/dx")),
            MindmapBranch("Projectile Motion", 0xFFF59E0B, listOf("T = 2u sinθ / g", "H = u² sin²θ / (2g)", "R = u² sin2θ / g", "Trajectory: y = x tanθ − gx²/(2u²cos²θ)")),
            MindmapBranch("Relative Motion", 0xFF8B5CF6, listOf("v_AB = v_A − v_B", "River-boat shortest path: sinθ = v_r / v_b", "Rain-umbrella: v_rain,man = v_rain − v_man"))
        ),
        "Laws of Motion" to listOf(
            MindmapBranch("Newton's Laws", 0xFF2563EB, listOf("1st: Inertia & Equilibrium", "2nd: F_net = dp/dt = ma", "3rd: Equal & opposite pair on diff bodies", "Impulse J = Δp = ∫F dt")),
            MindmapBranch("Friction", 0xFF10B981, listOf("Static f_s ≤ μ_s N", "Kinetic f_k = μ_k N", "Angle of repose tanθ = μ_s", "Pulling easier than pushing")),
            MindmapBranch("Circular Dynamics", 0xFFF59E0B, listOf("a_c = v²/R = ω²R", "Banking without friction: tanθ = v²/(Rg)", "Conical pendulum T cosθ = mg")),
            MindmapBranch("Pseudo Force", 0xFF8B5CF6, listOf("F_pseudo = −m · a_frame", "Apparent weight in lift: N = m(g ± a)", "Pulley constraint Σ T·a = 0"))
        ),
        "Electrostatics" to listOf(
            MindmapBranch("Coulomb & Field", 0xFF2563EB, listOf("F = kq₁q₂ / r²", "E = F/q = kq / r²", "Dipole E_axial = 2kp / r³", "Torque τ = p × E")),
            MindmapBranch("Gauss's Law", 0xFF10B981, listOf("Φ = ∮ E·dA = q_in / ε₀", "Infinite wire E = λ / (2πε₀r)", "Plane sheet E = σ / (2ε₀)", "Hollow sphere interior E = 0")),
            MindmapBranch("Potential & Work", 0xFFF59E0B, listOf("V = kq / r", "E = −∇V", "Equipotential ⊥ Field lines", "U = −p · E")),
            MindmapBranch("Capacitors", 0xFF8B5CF6, listOf("C = ε₀A / d", "Energy U = ½CV² = Q²/(2C)", "Dielectric K multiplies C", "Series: 1/C = Σ1/C_i, Parallel: C = ΣC_i"))
        ),
        "Chemical Bonding" to listOf(
            MindmapBranch("VSEPR & Shapes", 0xFF2563EB, listOf("LP-LP > LP-BP > BP-BP repulsion", "sp: Linear (180°)", "sp²: Trigonal planar (120°)", "sp³: Tetrahedral (109.5°)")),
            MindmapBranch("Hybridisation", 0xFF10B981, listOf("H = ½(V + M − C + A)", "sp³d: TBP (PCl₅)", "sp³d²: Octahedral (SF₆)", "dsp²: Square planar")),
            MindmapBranch("MOT (Molecular Orbital)", 0xFFF59E0B, listOf("Bond Order = ½(N_b − N_a)", "O₂ paramagnetic (2 unpaired π*)", "N₂ diamagnetic (BO = 3)", "14 e⁻ → max stability")),
            MindmapBranch("H-Bonding & Fajans", 0xFF8B5CF6, listOf("H-bond requires F, O, N", "o-Nitrophenol: intra (steam volatile)", "Fajans: Small cation + large anion → covalent"))
        ),
        "GOC" to listOf(
            MindmapBranch("Electronic Effects", 0xFF2563EB, listOf("Inductive (−I/+I): σ-bond distance dependent", "Resonance (±R/±M): π-delocalization", "Hyperconjugation: α-H count", "Order: +M > +H > +I")),
            MindmapBranch("Aromaticity (Hückel)", 0xFF10B981, listOf("Cyclic, planar, fully conjugated", "(4n + 2) π electrons → Aromatic", "4n π electrons → Anti-aromatic", "Pyridine & Furan examples")),
            MindmapBranch("Reactive Intermediates", 0xFFF59E0B, listOf("Carbocation sp² planar: 3° > 2° > 1°", "Carbanion sp³: 1° > 2° > 3°", "Free radical stability matches carbocation")),
            MindmapBranch("Acid / Base Strength", 0xFF8B5CF6, listOf("Acidity ∝ stability of conjugate base", "−I/−M groups increase acidity", "Ortho effect boosts benzoic acid strength"))
        ),
        "Calculus" to listOf(
            MindmapBranch("Limits & Continuity", 0xFF2563EB, listOf("0/0 or ∞/∞ → L'Hôpital / Expansion", "1^∞ → e^lim(f−1)g", "LHL = RHL = f(a) for continuity")),
            MindmapBranch("Differentiability", 0xFF10B981, listOf("LHD = RHD finite", "|x − a| non-differentiable at x = a", "Rolle's & Lagrange MVT")),
            MindmapBranch("Applications (AOD)", 0xFFF59E0B, listOf("Tangent slope dy/dx", "Maxima: f'(c)=0, f''(c)<0", "Monotonicity: f'(x) > 0 increasing")),
            MindmapBranch("Definite Integration", 0xFF8B5CF6, listOf("King's Property: f(a+b−x)", "Even/Odd symmetry on [−a, a]", "Leibnitz differentiation under integral"))
        ),
        "Genetics" to listOf(
            MindmapBranch("Mendelian Laws", 0xFF2563EB, listOf("Monohybrid 3:1 phenotype / 1:2:1 geno", "Dihybrid 9:3:3:1 ratio", "Test cross: 1:1", "Gametes = 2^n")),
            MindmapBranch("Deviations", 0xFF10B981, listOf("Incomplete dominance (Snapdragon 1:2:1)", "Co-dominance (ABO I^A I^B)", "Pleiotropy & Polygenic skin color")),
            MindmapBranch("Molecular DNA", 0xFFF59E0B, listOf("Chargaff: A=T, G=C", "Semiconservative (Meselson-Stahl)", "Leading 5'→3' continuous, Okazaki fragments")),
            MindmapBranch("Transcription & Operon", 0xFF8B5CF6, listOf("RNA Pol I(rRNA), II(mRNA), III(tRNA)", "Lac Operon: Inducible by allolactose", "64 codons: 61 sense + 3 stop (UAA/UAG/UGA)"))
        )
    )

    fun getMindmapsFor(exam: String, subject: String): List<ChapterMindmap> {
        val chapters = SyllabusChapters.chaptersFor(exam, subject)
        return chapters.mapIndexed { idx, chapter ->
            val matchedKey = curatedSummaries.keys.firstOrNull { chapter.contains(it, ignoreCase = true) }
            val branches = if (matchedKey != null) {
                curatedSummaries[matchedKey]!!
            } else {
                buildChapterBranches(exam, subject, chapter, idx)
            }
            ChapterMindmap(
                id = "${exam}_${subject}_mm_${idx + 1}",
                exam = exam,
                subject = subject,
                chapter = chapter,
                centralSummary = "High-yield $exam visual blueprint for $chapter — core formulas, exceptions, and PYQ traps.",
                branches = branches
            )
        }
    }

    private fun buildChapterBranches(exam: String, subject: String, chapter: String, idx: Int): List<MindmapBranch> {
        return listOf(
            MindmapBranch(
                "1. Core Laws & Definitions",
                subjectPalettes[0],
                listOf(
                    "Foundational principles of $chapter",
                    "Standard SI units, dimensions & sign conventions",
                    "Boundary conditions tested in $exam"
                )
            ),
            MindmapBranch(
                "2. Key Equations & Formulas",
                subjectPalettes[1],
                listOf(
                    "Primary governing relation & proportionality rules",
                    "Limiting cases (x→0, ∞, equilibrium state)",
                    if (subject == "Biology") "NCERT key terms, enzymes & numerical ratios" else "Graphical slope & area interpretations"
                )
            ),
            MindmapBranch(
                "3. Exam Traps & Exceptions",
                subjectPalettes[2],
                listOf(
                    "Verify unit consistency before substituting values",
                    "Check special exceptions highlighted in NCERT/PYQs",
                    "Eliminate dimensionally invalid MCQ options fast"
                )
            ),
            MindmapBranch(
                "4. $exam PYQ Strategy",
                subjectPalettes[3],
                listOf(
                    "Expected weightage: ${1 + (idx % 3)}–${2 + (idx % 3)} questions",
                    "Target solve time: ${if (exam == "NEET") "45–60 sec" else "90–120 sec"}",
                    "Cross-link with ${SyllabusChapters.chaptersFor(exam, subject)[(idx + 1) % SyllabusChapters.chaptersFor(exam, subject).size]}"
                )
            )
        )
    }
}
