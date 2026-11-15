package com.example.data.content

import com.example.data.model.QuestionItem

object BiologyQuestionsGenerator {

    private data class BioFact(
        val topic: String,
        val prompt: String,
        val answer: String,
        val d1: String,
        val d2: String,
        val d3: String,
        val explanation: String
    )

    private val factBank = listOf(
        BioFact("Taxonomy", "Binomial nomenclature system was introduced by:", "Carolus Linnaeus", "R.H. Whittaker", "Bentham and Hooker", "Ernst Mayr", "Carolus Linnaeus standardized Genus + species binomial nomenclature in Systema Naturae."),
        BioFact("Five Kingdoms", "Five Kingdom Classification (Monera, Protista, Fungi, Plantae, Animalia) was proposed by:", "R.H. Whittaker", "Carl Woese", "Linnaeus", "Aristotle", "R.H. Whittaker proposed the 5-kingdom classification in 1969 based on cell structure, nutrition, and phylogeny."),
        BioFact("Cell Biology", "The organelle known as the powerhouse of the cell synthesizing ATP by oxidative phosphorylation is:", "Mitochondria", "Golgi apparatus", "Lysosome", "Peroxisome", "Mitochondria carry the inner-membrane F0-F1 ATP synthase and electron transport chain."),
        BioFact("Cell Cycle", "Crossing over between non-sister chromatids of homologous chromosomes occurs in:", "Pachytene of Prophase I", "Zygotene of Prophase I", "Metaphase II", "Anaphase I", "Recombinase-mediated crossing over takes place during pachytene of meiosis I."),
        BioFact("Biomolecules", "In DNA double helix, Guanine pairs with Cytosine via how many hydrogen bonds?", "Three hydrogen bonds", "Two hydrogen bonds", "One ionic bond", "Four phosphodiester bonds", "G≡C base pairing utilizes three hydrogen bonds while A=T uses two."),
        BioFact("Photosynthesis", "The primary CO₂ acceptor in C4 plants (such as maize and sugarcane) is:", "Phosphoenolpyruvate (PEP)", "RuBP (5-carbon)", "3-Phosphoglycerate", "Oxaloacetic acid", "Mesophyll PEP carboxylase fixes CO₂ onto 3C PEP to form 4C OAA."),
        BioFact("Plant Hormones", "Which gaseous plant hormone stimulates fruit ripening and respiratory climacteric?", "Ethylene", "Auxin (IAA)", "Gibberellin", "Abscisic acid", "Ethylene is the gaseous phytohormone regulating senescence and fruit ripening."),
        BioFact("Human Digestion", "Which enzyme in human gastric juice converts inactive pepsinogen into active pepsin?", "Hydrochloric acid (HCl) & Pepsin autocatalysis", "Amylase", "Trypsinogen", "Bile salts", "Parietal cells secrete HCl (pH ~1.8) activating pepsinogen into pepsin."),
        BioFact("Circulation", "Which pacemaker structure initiates the normal rhythmic heartbeat in humans?", "Sinoatrial node (SA node)", "AV node", "Bundle of His", "Purkinje fibres", "SA node in the upper right atrium generates 70–75 action potentials/min."),
        BioFact("Excretion", "The structural and functional filtration unit of the human kidney is:", "Nephron", "Neuron", "Alveolus", "Hepatocyte", "Each human kidney contains ~1 million nephrons performing ultrafiltration, reabsorption, and secretion."),
        BioFact("Locomotion", "Calcium ions bind to which regulatory protein on thin actin filaments to initiate muscle contraction?", "Troponin", "Myosin head", "Titin", "Dystrophin", "Ca²⁺ binds Troponin-C, exposing myosin-binding sites on actin."),
        BioFact("Neural Control", "Myelin sheath around peripheral nerve axons is produced by:", "Schwann cells", "Oligodendrocytes", "Astrocytes", "Microglia", "Schwann cells myelinate PNS axons while oligodendrocytes myelinate CNS axons."),
        BioFact("Endocrine", "Insulin, which lowers blood glucose levels by promoting cellular glucose uptake, is secreted by:", "Beta cells of Islets of Langerhans", "Alpha cells of pancreas", "Adrenal medulla", "Thyroid follicular cells", "Pancreatic beta cells secrete insulin; alpha cells secrete glucagon."),
        BioFact("Reproduction", "Which hormone surge triggers ovulation around day 14 of the human menstrual cycle?", "LH (Luteinizing Hormone) surge", "Progesterone surge", "Oxytocin surge", "Prolactin surge", "Mid-cycle LH surge induces rupture of the Graafian follicle."),
        BioFact("Genetics", "Mendel's law of independent assortment holds strictly true when genes are located:", "On non-homologous chromosomes or far apart", "Tightly linked on the same chromosome", "Within mitochondrial DNA", "Only on the Y-chromosome", "Independent assortment fails for closely linked genes due to reduced recombination."),
        BioFact("Molecular Genetics", "During transcription, which enzyme catalyzes synthesis of mRNA from the DNA template strand?", "DNA-dependent RNA polymerase", "DNA polymerase III", "Reverse transcriptase", "DNA ligase", "RNA polymerase reads DNA 3'→5' to synthesize RNA 5'→3'."),
        BioFact("Evolution", "Wings of butterfly (insect) and wings of bird are classic examples of:", "Analogous organs (convergent evolution)", "Homologous organs (divergent evolution)", "Vestigial organs", "Atavism", "Different anatomical origin performing similar flight function exemplifies convergent evolution."),
        BioFact("Human Health", "Widal test is clinically used as a diagnostic confirmatory test for:", "Typhoid fever (Salmonella typhi)", "Malaria", "Dengue", "Tuberculosis", "Widal detects agglutinating antibodies against Salmonella typhi O and H antigens."),
        BioFact("Biotechnology", "Restriction endonucleases recognize specific sequences in DNA known as:", "Palindromic nucleotide sequences", "Poly-A tails", "Intron splice sites", "Telomeric repeats", "Type II restriction enzymes (e.g., EcoRI 5'-GAATTC-3') cut palindromic DNA sequences."),
        BioFact("Ecology", "Pyramid of energy in any natural terrestrial or aquatic ecosystem is always:", "Upright", "Inverted", "Spindle-shaped", "Rhombic", "Following Lindeman's 10% law, energy always decreases from producers to top trophic levels.")
    )

    fun generate(targetCount: Int = 1050): List<QuestionItem> {
        val chapters = SyllabusChapters.neetBiology
        val list = ArrayList<QuestionItem>(targetCount)
        list.addAll(curatedBiology(chapters))

        var idx = list.size
        while (list.size < targetCount) {
            val chapter = chapters[idx % chapters.size]
            val templateIndex = idx % 14
            val variant = idx / 14 + 1
            val diff = QuestionBankHelper.difficultyForIndex(idx)
            val id = "NEET_BIO_${(list.size + 1).toString().padStart(4, '0')}"
            list.add(buildTemplateQuestion(id, chapter, diff, templateIndex, variant, idx))
            idx++
        }
        return list
    }

    private fun buildTemplateQuestion(
        id: String,
        chapter: String,
        diff: String,
        t: Int,
        v: Int,
        seed: Int
    ): QuestionItem {
        return when (t) {
            in 0..5 -> {
                val f = factBank[(v + t) % factBank.size]
                QuestionBankHelper.makeQuestion(
                    id, "NEET", "Biology", chapter, diff,
                    "[#$v · ${f.topic}] ${f.prompt}",
                    f.answer, f.d1, f.d2, f.d3, f.explanation, seed
                )
            }
            6 -> {
                val divisions = 3 + (v % 8)
                val cells = 1 shl divisions
                QuestionBankHelper.makeQuestion(
                    id, "NEET", "Biology", chapter, diff,
                    "[#$v] Starting from a single parent cell undergoing $divisions successive mitotic divisions, how many daughter cells are formed?",
                    "$cells cells", "${divisions * 2} cells", "${cells * 2} cells", "${divisions * divisions} cells",
                    "Each mitotic cycle doubles cell count: 2^$divisions = $cells daughter cells.", seed
                )
            }
            7 -> {
                val nChromosomes = 8 + (v % 16)
                val twoN = 2 * nChromosomes
                QuestionBankHelper.makeQuestion(
                    id, "NEET", "Biology", chapter, diff,
                    "[#$v] If the somatic leaf cell of an angiosperm plant has $twoN chromosomes (2n), how many chromosomes are present in its endosperm (3n)?",
                    "${3 * nChromosomes}", "$twoN", "$nChromosomes", "${4 * nChromosomes}",
                    "Since 2n = $twoN, haploid n = $nChromosomes. Primary endosperm nucleus is triploid (3n) = 3 × $nChromosomes = ${3 * nChromosomes}.", seed
                )
            }
            8 -> {
                val aminoAcids = 20 + (v % 30) * 5
                val codons = aminoAcids + 1 // +1 stop codon
                val bases = codons * 3
                QuestionBankHelper.makeQuestion(
                    id, "NEET", "Biology", chapter, diff,
                    "[#$v] How many mRNA nucleotides (including one termination codon) are minimally needed to translate a polypeptide of $aminoAcids amino acids?",
                    "$bases nucleotides", "${aminoAcids * 3} nucleotides", "${aminoAcids * 2} nucleotides", "${bases + 3} nucleotides",
                    "Each amino acid uses 1 triplet codon (3 bases) plus 1 stop codon: ($aminoAcids + 1) × 3 = $bases nucleotides.", seed
                )
            }
            9 -> {
                val adeninePct = 16 + (v % 18)
                val cytosinePct = 50 - adeninePct
                QuestionBankHelper.makeQuestion(
                    id, "NEET", "Biology", chapter, diff,
                    "[#$v] By Chargaff's rules, if a double-stranded DNA sample contains $adeninePct% Adenine, what is the percentage of Cytosine?",
                    "$cytosinePct%", "$adeninePct%", "${100 - adeninePct}%", "${adeninePct * 2}%",
                    "In dsDNA, A = T = $adeninePct% (sum = ${2 * adeninePct}%). Remaining ${100 - 2 * adeninePct}% splits equally: G = C = $cytosinePct%.", seed
                )
            }
            10 -> {
                val producerKcal = 10000 * (1 + (v % 5))
                val primaryKcal = producerKcal / 10
                val secondaryKcal = primaryKcal / 10
                QuestionBankHelper.makeQuestion(
                    id, "NEET", "Biology", chapter, diff,
                    "[#$v] Applying Lindeman's 10% energy transfer law, if producers trap $producerKcal kcal of energy, how much reaches secondary consumers (third trophic level)?",
                    "$secondaryKcal kcal", "$primaryKcal kcal", "${secondaryKcal / 10} kcal", "${producerKcal / 5} kcal",
                    "Producers ($producerKcal) -> Primary consumers ($primaryKcal) -> Secondary consumers ($secondaryKcal kcal).", seed
                )
            }
            11 -> {
                val hr = 65 + (v % 20)
                val sv = 70
                val co = hr * sv
                QuestionBankHelper.makeQuestion(
                    id, "NEET", "Biology", chapter, diff,
                    "[#$v] If an athlete's heart rate is $hr beats/min and stroke volume is $sv mL, calculate the cardiac output.",
                    "$co mL/min", "${hr + sv} mL/min", "${co / 2} mL/min", "${sv * 100} mL/min",
                    "Cardiac Output = Heart Rate × Stroke Volume = $hr × $sv = $co mL/min.", seed
                )
            }
            12 -> {
                val nPairs = 1 + (v % 4)
                val gametes = 1 shl nPairs
                val geno = when (nPairs) {
                    1 -> "Aa"
                    2 -> "AaBb"
                    3 -> "AaBbCc"
                    else -> "AaBbCcDd"
                }
                QuestionBankHelper.makeQuestion(
                    id, "NEET", "Biology", chapter, diff,
                    "[#$v] How many genetically distinct gametes can be formed by an organism with independently assorting genotype $geno?",
                    "$gametes", "${nPairs * 2}", "${gametes * 2}", "${nPairs * nPairs}",
                    "Number of gamete types = 2^n where n = number of heterozygous loci ($nPairs) => 2^$nPairs = $gametes.", seed
                )
            }
            else -> {
                val bpLen = 100 * (1 + (v % 10))
                val turns = QuestionBankHelper.round2(bpLen / 10.0)
                QuestionBankHelper.makeQuestion(
                    id, "NEET", "Biology", chapter, diff,
                    "[#$v] A B-DNA segment contains $bpLen base pairs. Approximately how many complete helical turns does it make (10 bp/turn)?",
                    "$turns turns", "${bpLen * 10} turns", "${QuestionBankHelper.round2(bpLen / 34.0)} turns", "${bpLen / 2} turns",
                    "B-DNA completes 1 full 360° turn every 10 base pairs (3.4 nm), giving $bpLen / 10 = $turns turns.", seed
                )
            }
        }
    }

    private fun curatedBiology(chapters: List<String>): List<QuestionItem> = listOf(
        QuestionBankHelper.makeQuestion(
            "NEET_BIO_0001", "NEET", "Biology", chapters[3 % chapters.size], "Easy",
            "Which cell organelle is directly responsible for packaging and modification of glycoproteins?",
            "Golgi apparatus", "Ribosome", "Centrosome", "Vacuole",
            "Golgi cisternae modify proteins and lipids into glycoproteins and glycolipids.", 301
        ),
        QuestionBankHelper.makeQuestion(
            "NEET_BIO_0002", "NEET", "Biology", chapters[5 % chapters.size], "Medium",
            "Photolysis of water during light reaction of photosynthesis requires which mineral ions?",
            "Manganese (Mn²⁺) and Chloride (Cl⁻)", "Magnesium and Iron", "Sodium and Potassium", "Copper and Zinc",
            "The Oxygen Evolving Complex (PS II) depends on Mn²⁺ and Cl⁻ ions to split H₂O into O₂, H⁺, and e⁻.", 302
        ),
        QuestionBankHelper.makeQuestion(
            "NEET_BIO_0003", "NEET", "Biology", chapters[7 % chapters.size], "Medium",
            "Maximum volume of air a person can breathe in after a forced expiration is called:",
            "Vital Capacity (VC)", "Tidal Volume (TV)", "Residual Volume (RV)", "Inspiratory Reserve Volume",
            "Vital Capacity = ERV + TV + IRV (~3500–4500 mL).", 303
        ),
        QuestionBankHelper.makeQuestion(
            "NEET_BIO_0004", "NEET", "Biology", chapters[12 % chapters.size], "Tough",
            "Which triplet codon acts both as the start/initiation codon and codes for Methionine in eukaryotes?",
            "AUG", "UAA", "UGA", "GUG",
            "AUG initiates translation and codes for Methionine; UAA, UAG, UGA are stop codons.", 304
        ),
        QuestionBankHelper.makeQuestion(
            "NEET_BIO_0005", "NEET", "Biology", chapters[13 % chapters.size], "Tough",
            "Taq DNA polymerase used in Polymerase Chain Reaction (PCR) is isolated from:",
            "Thermus aquaticus", "Escherichia coli", "Agrobacterium tumefaciens", "Bacillus thuringiensis",
            "Thermus aquaticus thrives in hot springs; its Taq polymerase stays stable during 94°C denaturation.", 305
        )
    )
}
