package com.example.data.content

import com.example.data.model.QuestionItem
import kotlin.math.abs
import kotlin.math.roundToInt

object ChemistryQuestionsGenerator {

    fun generate(exam: String, targetCount: Int = 1050): List<QuestionItem> {
        val chapters = SyllabusChapters.chaptersFor(exam, "Chemistry")
        val list = ArrayList<QuestionItem>(targetCount)
        list.addAll(curatedChemistry(exam, chapters))

        var idx = list.size
        while (list.size < targetCount) {
            val chapter = chapters[idx % chapters.size]
            val templateIndex = idx % 16
            val variant = idx / 16 + 1
            val diff = QuestionBankHelper.difficultyForIndex(idx)
            val id = "${exam}_CHEM_${(list.size + 1).toString().padStart(4, '0')}"
            list.add(buildTemplateQuestion(id, exam, chapter, diff, templateIndex, variant, idx))
            idx++
        }
        return list
    }

    private fun buildTemplateQuestion(
        id: String,
        exam: String,
        chapter: String,
        diff: String,
        t: Int,
        v: Int,
        seed: Int
    ): QuestionItem {
        return when (t) {
            0 -> {
                val massG = 18 * (1 + (v % 12))
                val moles = massG / 18
                QuestionBankHelper.makeQuestion(
                    id, exam, "Chemistry", chapter, diff,
                    "[#$v] Calculate the number of moles present in $massG g of pure H₂O (Molar mass = 18 g/mol).",
                    "$moles mol", "${moles * 2} mol", "${QuestionBankHelper.round2(moles / 2.0)} mol", "${massG} mol",
                    "Moles n = given mass / molar mass = $massG / 18 = $moles mol.", seed
                )
            }
            1 -> {
                val n = 2 + (v % 4)
                val l = 1 + (v % 2)
                val orbital = when (l) {
                    0 -> "${n}s"
                    1 -> "${n}p"
                    2 -> "${n}d"
                    else -> "${n}f"
                }
                val nPlusL = n + l
                QuestionBankHelper.makeQuestion(
                    id, exam, "Chemistry", chapter, diff,
                    "[#$v] Find the value of (n + l) for an electron occupying the $orbital subshell according to the Aufbau rule.",
                    "$nPlusL", "${nPlusL + 1}", "${nPlusL - 1}", "${n * l + 1}",
                    "For $orbital subshell, principal quantum number n = $n and azimuthal quantum number l = $l, hence (n + l) = $nPlusL.", seed
                )
            }
            2 -> {
                val atomicNum = 11 + (v % 19) // 11..29
                val elem = when (atomicNum) {
                    11 -> "Na (Z=11)"
                    12 -> "Mg (Z=12)"
                    13 -> "Al (Z=13)"
                    14 -> "Si (Z=14)"
                    15 -> "P (Z=15)"
                    16 -> "S (Z=16)"
                    17 -> "Cl (Z=17)"
                    19 -> "K (Z=19)"
                    20 -> "Ca (Z=20)"
                    21 -> "Sc (Z=21)"
                    24 -> "Cr (Z=24)"
                    25 -> "Mn (Z=25)"
                    26 -> "Fe (Z=26)"
                    29 -> "Cu (Z=29)"
                    else -> "Element Z=$atomicNum"
                }
                val block = when {
                    atomicNum in listOf(11, 12, 19, 20) -> "s-block"
                    atomicNum in 13..18 -> "p-block"
                    else -> "d-block"
                }
                val otherBlocks = listOf("s-block", "p-block", "d-block", "f-block").filter { it != block }
                QuestionBankHelper.makeQuestion(
                    id, exam, "Chemistry", chapter, diff,
                    "[#$v] Identify the periodic table block to which $elem belongs in its ground state.",
                    block, otherBlocks[0], otherBlocks[1], otherBlocks[2],
                    "Based on the last differentiating electron entering the valence subshell, $elem lies in the $block.", seed
                )
            }
            3 -> {
                val bondPairs = listOf(
                    Triple("BF₃", "sp² (Trigonal planar)", "sp³ (Tetrahedral)"),
                    Triple("CH₄", "sp³ (Tetrahedral)", "sp² (Trigonal planar)"),
                    Triple("SF₆", "sp³d² (Octahedral)", "sp³d (Trigonal bipyramidal)"),
                    Triple("PCl₅", "sp³d (Trigonal bipyramidal)", "sp³d² (Octahedral)"),
                    Triple("BeCl₂ (vapour)", "sp (Linear)", "sp² (Bent)"),
                    Triple("XeF₄", "sp³d² (Square planar)", "sp³ (Tetrahedral)")
                )
                val item = bondPairs[v % bondPairs.size]
                QuestionBankHelper.makeQuestion(
                    id, exam, "Chemistry", chapter, diff,
                    "[#$v] According to VSEPR and hybridisation theory, what is the hybridisation and geometry of ${item.first}?",
                    item.second, item.third, "dsp² (Square pyramidal)", "sp³d³ (Pentagonal bipyramidal)",
                    "Counting sigma bond pairs + lone pairs around the central atom gives ${item.second} for ${item.first}.", seed
                )
            }
            4 -> {
                val concPow = 1 + (v % 6)
                val labelConc = "10⁻$concPow M"
                QuestionBankHelper.makeQuestion(
                    id, exam, "Chemistry", chapter, diff,
                    "[#$v] At 298 K, what is the pH of a dilute aqueous HCl solution of concentration $labelConc (assume complete dissociation)?",
                    "$concPow.0", "${14 - concPow}.0", "${concPow - 1}.5", "${concPow + 2}.0",
                    "Since HCl is a strong monobasic acid, [H⁺] = 10⁻$concPow M, so pH = −log₁₀(10⁻$concPow) = $concPow.0.", seed
                )
            }
            5 -> {
                val pOHPow = 2 + (v % 5)
                val pH = 14 - pOHPow
                QuestionBankHelper.makeQuestion(
                    id, exam, "Chemistry", chapter, diff,
                    "[#$v] At 298 K (K_w = 10⁻¹⁴), an aqueous NaOH solution has concentration 10⁻$pOHPow M. What is its pH?",
                    "$pH.0", "$pOHPow.0", "${pH - 2}.0", "${7 + (pOHPow % 2)}.0",
                    "For strong base NaOH, [OH⁻] = 10⁻$pOHPow M => pOH = $pOHPow => pH = 14 − $pOHPow = $pH.0.", seed
                )
            }
            6 -> {
                val molesSolute = 1 + (v % 5)
                val volLitre = 2 + (v % 4)
                val molarity = QuestionBankHelper.round2(molesSolute.toDouble() / volLitre)
                QuestionBankHelper.makeQuestion(
                    id, exam, "Chemistry", chapter, diff,
                    "[#$v] $molesSolute mol of glucose is dissolved to make $volLitre L of aqueous solution. What is the molarity?",
                    "$molarity M", "${molesSolute * volLitre} M", "${QuestionBankHelper.round2(volLitre.toDouble() / molesSolute)} M", "${QuestionBankHelper.round2(molesSolute.toDouble() / (volLitre + 1))} M",
                    "Molarity M = moles of solute / volume of solution in litres = $molesSolute / $volLitre = $molarity M.", seed
                )
            }
            7 -> {
                val q = 400 + (v % 8) * 100
                val w = 150 + (v % 6) * 50
                val deltaU = q - w
                QuestionBankHelper.makeQuestion(
                    id, exam, "Chemistry", chapter, diff,
                    "[#$v] A closed thermodynamic system absorbs $q J of heat and performs $w J of expansion work. Calculate ΔU (First Law).",
                    "+$deltaU J", "+${q + w} J", "-$deltaU J", "+${deltaU / 2} J",
                    "By First Law (IUPAC convention): ΔU = q + w = +$q + (-$w) = +$deltaU J.", seed
                )
            }
            8 -> {
                val deltaH = -200 - (v % 6) * 40
                val temp = 300 + (v % 4) * 50
                val deltaS = -100 - (v % 5) * 20 // J/K
                val deltaG = deltaH - (temp * deltaS / 1000.0)
                val ans = QuestionBankHelper.round2(deltaG)
                QuestionBankHelper.makeQuestion(
                    id, exam, "Chemistry", chapter, diff,
                    "[#$v] For a reaction, ΔH = $deltaH kJ/mol and ΔS = $deltaS J/(mol·K) at T = $temp K. Calculate ΔG.",
                    "$ans kJ/mol", "${QuestionBankHelper.round2(deltaH + temp * deltaS / 1000.0)} kJ/mol", "${deltaH - deltaS} kJ/mol", "0 kJ/mol",
                    "ΔG = ΔH − TΔS = $deltaH − ($temp × (${deltaS / 1000.0})) = $ans kJ/mol.", seed
                )
            }
            9 -> {
                val halfLifeMin = 10 + (v % 8) * 5
                val kVal = QuestionBankHelper.round2(0.693 / halfLifeMin)
                QuestionBankHelper.makeQuestion(
                    id, exam, "Chemistry", chapter, diff,
                    "[#$v] A first-order reaction has a half-life t₁/₂ = $halfLifeMin min. Find its rate constant k (use ln 2 = 0.693).",
                    "$kVal min⁻¹", "${QuestionBankHelper.round2(halfLifeMin / 0.693)} min⁻¹", "${QuestionBankHelper.round2(1.0 / halfLifeMin)} min⁻¹", "${QuestionBankHelper.round2(0.693 * halfLifeMin)} min⁻¹",
                    "For a first-order reaction, k = 0.693 / t₁/₂ = 0.693 / $halfLifeMin = $kVal min⁻¹.", seed
                )
            }
            10 -> {
                val oxStates = listOf(
                    Triple("KMnO₄ (Mn)", "+7", "+5"),
                    Triple("K₂Cr₂O₇ (Cr)", "+6", "+3"),
                    Triple("H₂SO₄ (S)", "+6", "+4"),
                    Triple("HNO₃ (N)", "+5", "+3"),
                    Triple("HClO₄ (Cl)", "+7", "+5"),
                    Triple("Fe(CO)₅ (Fe)", "0", "+2")
                )
                val pick = oxStates[v % oxStates.size]
                QuestionBankHelper.makeQuestion(
                    id, exam, "Chemistry", chapter, diff,
                    "[#$v] Determine the oxidation state of the indicated central atom in ${pick.first}.",
                    pick.second, pick.third, "+1", "+4",
                    "Summing known oxidation numbers in neutral molecule ${pick.first} yields ${pick.second}.", seed
                )
            }
            11 -> {
                val eCathode = 0.34 + (v % 4) * 0.2
                val eAnode = -0.76 + (v % 3) * 0.12
                val eCell = QuestionBankHelper.round2(eCathode - eAnode)
                QuestionBankHelper.makeQuestion(
                    id, exam, "Chemistry", chapter, diff,
                    "[#$v] Given E°_cathode = +${QuestionBankHelper.round2(eCathode)} V and E°_anode = ${QuestionBankHelper.round2(eAnode)} V. Calculate E°_cell.",
                    "+$eCell V", "${QuestionBankHelper.round2(eCathode + eAnode)} V", "${QuestionBankHelper.round2(eAnode - eCathode)} V", "+0.10 V",
                    "E°_cell = E°_cathode − E°_anode = ${QuestionBankHelper.round2(eCathode)} − (${QuestionBankHelper.round2(eAnode)}) = +$eCell V.", seed
                )
            }
            12 -> {
                val reagents = listOf(
                    Triple("Aldol Condensation", "Dilute NaOH / Δ on α-H aldehyde", "Zn-Hg / Conc. HCl"),
                    Triple("Cannizzaro Reaction", "Conc. KOH on aldehyde lacking α-H", "AlCl₃ / CH₃Cl"),
                    Triple("Clemmensen Reduction", "Zn-Hg + Conc. HCl", "NH₂NH₂ / KOH in glycol"),
                    Triple("Hoffmann Bromamide", "Br₂ + aqueous/ethanolic KOH on amide", "NaNO₂ + HCl at 0–5°C"),
                    Triple("Sandmeyer Reaction", "Cu₂Cl₂ / HCl on benzenediazonium salt", "H₂ / Pd-BaSO₄")
                )
                val r = reagents[v % reagents.size]
                QuestionBankHelper.makeQuestion(
                    id, exam, "Chemistry", chapter, diff,
                    "[#$v] Which reagent condition is characteristic of the ${r.first}?",
                    r.second, r.third, "PCl₅ + Pyridine", "KMnO₄ / H⁺ reflux",
                    "${r.first} specifically proceeds with ${r.second}.", seed
                )
            }
            13 -> {
                val cAtoms = 2 + (v % 6)
                val nH = 2 * cAtoms + 2
                val sigmaBonds = cAtoms - 1 + nH
                QuestionBankHelper.makeQuestion(
                    id, exam, "Chemistry", chapter, diff,
                    "[#$v] How many total sigma (σ) covalent bonds exist in an open-chain saturated alkane C_${cAtoms}H_${nH}?",
                    "$sigmaBonds", "${sigmaBonds - 2}", "${sigmaBonds + 2}", "$nH",
                    "An alkane C_nH_(2n+2) has (n − 1) C–C σ-bonds and (2n + 2) C–H σ-bonds = ${cAtoms - 1} + $nH = $sigmaBonds.", seed
                )
            }
            14 -> {
                val unpaired = 1 + (v % 5)
                val spinOnly = QuestionBankHelper.round2(kotlin.math.sqrt((unpaired * (unpaired + 2)).toDouble()))
                QuestionBankHelper.makeQuestion(
                    id, exam, "Chemistry", chapter, diff,
                    "[#$v] A d-block coordination complex ion possesses $unpaired unpaired electrons. What is its spin-only magnetic moment (BM)?",
                    "$spinOnly BM", "${unpaired}.00 BM", "${QuestionBankHelper.round2(unpaired * 1.414)} BM", "${unpaired + 2}.00 BM",
                    "Spin-only magnetic moment μ = √(n(n + 2)) BM = √($unpaired × ${unpaired + 2}) = $spinOnly BM.", seed
                )
            }
            else -> {
                val p0 = 200 + (v % 5) * 40
                val xSolute = 0.1 + (v % 4) * 0.05
                val pSol = QuestionBankHelper.round2(p0 * (1.0 - xSolute))
                QuestionBankHelper.makeQuestion(
                    id, exam, "Chemistry", chapter, diff,
                    "[#$v] Vapour pressure of pure solvent is $p0 mmHg. A non-volatile solute is added with mole fraction ${QuestionBankHelper.round2(xSolute)}. Using Raoult's Law, solution vapour pressure is:",
                    "$pSol mmHg", "${QuestionBankHelper.round2(p0 * xSolute)} mmHg", "${p0} mmHg", "${QuestionBankHelper.round2(p0 * (1.0 + xSolute))} mmHg",
                    "By Raoult's Law: P_solution = P°_solvent × X_solvent = $p0 × (1 − ${QuestionBankHelper.round2(xSolute)}) = $pSol mmHg.", seed
                )
            }
        }
    }

    private fun curatedChemistry(exam: String, chapters: List<String>): List<QuestionItem> = listOf(
        QuestionBankHelper.makeQuestion(
            "${exam}_CHEM_0001", exam, "Chemistry", chapters[0 % chapters.size], "Easy",
            "Avogadro's constant N_A represents how many particles per mole?",
            "6.022 × 10²³ mol⁻¹", "6.022 × 10²² mol⁻¹", "1.66 × 10⁻²⁴ mol⁻¹", "3.011 × 10²³ mol⁻¹",
            "One mole of any substance contains exactly 6.02214076 × 10²³ elementary entities.", 101
        ),
        QuestionBankHelper.makeQuestion(
            "${exam}_CHEM_0002", exam, "Chemistry", chapters[3 % chapters.size], "Medium",
            "Which molecule among the following possesses zero net dipole moment despite polar bonds?",
            "BF₃", "NH₃", "H₂O", "NF₃",
            "BF₃ has symmetrical trigonal planar (sp²) geometry, so individual B–F bond dipoles cancel completely.", 102
        ),
        QuestionBankHelper.makeQuestion(
            "${exam}_CHEM_0003", exam, "Chemistry", chapters[5 % chapters.size], "Medium",
            "For the endothermic gas reaction N₂O₄(g) ⇌ 2NO₂(g), raising temperature shifts equilibrium towards:",
            "Forward direction (more NO₂)", "Backward direction (more N₂O₄)", "No shift in composition", "Zero equilibrium constant",
            "By Le Chatelier's principle, increasing temperature favours the endothermic forward direction (K_p rises).", 103
        ),
        QuestionBankHelper.makeQuestion(
            "${exam}_CHEM_0004", exam, "Chemistry", chapters[10 % chapters.size], "Tough",
            "Which carbocation intermediate has the greatest hyperconjugative and resonance stability?",
            "Benzyl carbocation (C₆H₅CH₂⁺)", "Methyl carbocation (CH₃⁺)", "Vinyl carbocation (CH₂=CH⁺)", "Ethyl carbocation (CH₃CH₂⁺)",
            "Benzyl carbocation delocalizes positive charge into the aromatic benzene ring across four resonance structures.", 104
        ),
        QuestionBankHelper.makeQuestion(
            "${exam}_CHEM_0005", exam, "Chemistry", chapters[9 % chapters.size], "Tough",
            "According to Crystal Field Theory, what is the geometry and magnetism of [Ni(CN)₄]²⁻?",
            "Square planar, diamagnetic (dsp²)", "Tetrahedral, paramagnetic (sp³)", "Octahedral, paramagnetic", "Linear, diamagnetic",
            "CN⁻ is a strong-field ligand causing pairing of d⁸ electrons in Ni²⁺, freeing one 3d orbital for dsp² square planar geometry.", 105
        )
    )
}
