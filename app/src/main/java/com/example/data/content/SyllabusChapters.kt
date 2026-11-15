package com.example.data.content

object SyllabusChapters {
    val jeePhysics = listOf(
        "Units & Measurements",
        "Kinematics & Projectiles",
        "Laws of Motion & Friction",
        "Work, Energy & Power",
        "Rotational Motion",
        "Gravitation",
        "Properties of Matter & Fluids",
        "Thermodynamics & KTG",
        "Oscillations (SHM) & Waves",
        "Electrostatics & Gauss Law",
        "Capacitance & Dielectrics",
        "Current Electricity",
        "Magnetism & Moving Charges",
        "Electromagnetic Induction & AC",
        "Ray & Wave Optics",
        "Modern Physics & Semiconductors"
    )

    val jeeChemistry = listOf(
        "Mole Concept & Stoichiometry",
        "Atomic Structure & Quantum",
        "Periodic Classification",
        "Chemical Bonding & VSEPR",
        "Chemical Thermodynamics",
        "Chemical & Ionic Equilibrium",
        "Redox & Electrochemistry",
        "Solutions & Chemical Kinetics",
        "p-Block, d & f Block Elements",
        "Coordination Compounds",
        "General Organic Chemistry (GOC)",
        "Hydrocarbons",
        "Haloalkanes, Alcohols & Phenols",
        "Aldehydes, Ketones & Acids",
        "Amines & Biomolecules"
    )

    val jeeMathematics = listOf(
        "Sets, Relations & Functions",
        "Complex Numbers & Quadratics",
        "Trigonometry & Equations",
        "Sequences & Series",
        "Permutations, Combinations & Binomial",
        "Straight Lines & Circles",
        "Conic Sections (Parabola, Ellipse, Hyperbola)",
        "Limits, Continuity & Differentiability",
        "Applications of Derivatives",
        "Indefinite & Definite Integration",
        "Differential Equations",
        "Matrices & Determinants",
        "Vector Algebra",
        "Three-Dimensional Geometry",
        "Probability & Statistics"
    )

    val neetPhysics = listOf(
        "Units & Error Analysis",
        "Motion in Straight Line & Plane",
        "Laws of Motion",
        "Work, Energy & Power",
        "System of Particles & Rotation",
        "Gravitation",
        "Thermal & Mechanical Properties",
        "Thermodynamics & KTG",
        "Oscillations & Waves",
        "Electrostatics & Capacitance",
        "Current Electricity",
        "Magnetism, EMI & AC",
        "Ray & Wave Optics",
        "Dual Nature, Atoms, Nuclei & Semiconductors"
    )

    val neetChemistry = listOf(
        "Some Basic Concepts of Chemistry",
        "Structure of Atom",
        "Classification & Periodicity",
        "Chemical Bonding",
        "Thermodynamics",
        "Equilibrium & pH",
        "Redox & Electrochemistry",
        "Solutions & Chemical Kinetics",
        "p-Block, d & f Block Elements",
        "Coordination Chemistry",
        "Organic Chemistry Basics & Isomerism",
        "Hydrocarbons & Haloalkanes",
        "Oxygen Containing Compounds",
        "Amines & Biomolecules"
    )

    val neetBiology = listOf(
        "Living World & Biological Classification",
        "Plant & Animal Kingdom",
        "Morphology & Anatomy of Plants",
        "Cell: The Unit of Life & Cell Cycle",
        "Biomolecules & Enzymes",
        "Photosynthesis & Plant Respiration",
        "Plant Growth & Hormones",
        "Breathing, Circulation & Body Fluids",
        "Excretory, Locomotion & Neural Control",
        "Chemical Coordination & Digestion",
        "Sexual Reproduction in Plants & Humans",
        "Principles of Inheritance & Variation",
        "Molecular Basis of Inheritance",
        "Evolution, Human Health & Biotechnology",
        "Organisms, Ecosystem & Biodiversity"
    )

    fun chaptersFor(exam: String, subject: String): List<String> = when (exam) {
        "JEE" -> when (subject) {
            "Physics" -> jeePhysics
            "Chemistry" -> jeeChemistry
            else -> jeeMathematics
        }
        else -> when (subject) {
            "Physics" -> neetPhysics
            "Chemistry" -> neetChemistry
            else -> neetBiology
        }
    }
}
