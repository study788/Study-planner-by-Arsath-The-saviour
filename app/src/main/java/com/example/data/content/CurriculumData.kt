package com.example.data.content

import com.example.data.model.ExamType
import com.example.data.model.PlanType
import com.example.data.model.PlannerRow
import com.example.data.model.QuoteItem

object CurriculumData {

    val quotes: List<QuoteItem> = listOf(
        QuoteItem("🔥", "Success is not final; failure is not fatal: it is the courage to continue that counts.", "Winston Churchill"),
        QuoteItem("⭐", "The secret of getting ahead is getting started.", "Mark Twain"),
        QuoteItem("💡", "An investment in knowledge pays the best interest.", "Benjamin Franklin"),
        QuoteItem("🏆", "Dreams don't work unless you do.", "John C. Maxwell"),
        QuoteItem("🎯", "It does not matter how slowly you go as long as you do not stop.", "Confucius"),
        QuoteItem("🚀", "The more that you read, the more things you will know.", "Dr. Seuss"),
        QuoteItem("💪", "Hardships often prepare ordinary people for an extraordinary destiny.", "C.S. Lewis"),
        QuoteItem("🌟", "You don't have to be great to start, but you have to start to be great.", "Zig Ziglar"),
        QuoteItem("📚", "Education is the most powerful weapon which you can use to change the world.", "Nelson Mandela"),
        QuoteItem("🎓", "The expert in anything was once a beginner.", "Helen Hayes"),
        QuoteItem("⚡", "Believe you can and you're halfway there.", "Theodore Roosevelt"),
        QuoteItem("🌈", "Your only limit is your mind.", "Unknown"),
        QuoteItem("🔬", "Science is not only a disciple of reason but also one of romance and passion.", "Stephen Hawking"),
        QuoteItem("🏅", "The difference between ordinary and extraordinary is that little extra.", "Jimmy Johnson"),
        QuoteItem("✨", "Push yourself, because no one else is going to do it for you.", "Unknown"),
        QuoteItem("💡", "Study hard what interests you the most in the most undisciplined way possible.", "Richard Feynman"),
        QuoteItem("🚀", "The future belongs to those who believe in the beauty of their dreams.", "Eleanor Roosevelt"),
        QuoteItem("🌟", "Don't watch the clock; do what it does — keep going.", "Sam Levenson"),
        QuoteItem("🔥", "Success usually comes to those who are too busy to be looking for it.", "Henry David Thoreau"),
        QuoteItem("⭐", "Opportunities don't happen, you create them.", "Chris Grosser"),
        QuoteItem("💪", "Do something today that your future self will thank you for.", "Sean Patrick Flanery"),
        QuoteItem("📚", "The beautiful thing about learning is that nobody can take it away from you.", "B.B. King"),
        QuoteItem("🎓", "To learn without thinking is labour lost; to think without learning is dangerous.", "Confucius"),
        QuoteItem("⚡", "A person who never made a mistake never tried anything new.", "Albert Einstein"),
        QuoteItem("🌈", "Strive not to be a success, but rather to be of value.", "Albert Einstein"),
        QuoteItem("🏆", "The more I practice, the luckier I get.", "Gary Player"),
        QuoteItem("🎯", "Motivation is what gets you started. Habit is what keeps you going.", "Jim Ryun"),
        QuoteItem("💡", "Even if you're on the right track, you'll get run over if you just sit there.", "Will Rogers"),
        QuoteItem("🚀", "It's not whether you get knocked down, it's whether you get up.", "Vince Lombardi"),
        QuoteItem("🔥", "Work hard in silence, let your success be your noise.", "Frank Ocean")
    )

    fun dailyQuote(dayOfMonth: Int): QuoteItem =
        quotes[dayOfMonth.coerceAtLeast(0) % quotes.size]

    fun getPlanRows(exam: ExamType, plan: PlanType): List<PlannerRow> = when (exam) {
        ExamType.JEE -> when (plan) {
            PlanType.ONE_YEAR -> listOf(
                PlannerRow("May '26", "Mechanics I: Kinematics, Laws of Motion", "Mole Concept, Atomic Structure", "Sets, Relations, Functions"),
                PlannerRow("Jun '26", "Mechanics II: Work, Energy, Momentum", "Chemical Bonding", "Sequence & Series, Complex Numbers"),
                PlannerRow("Jul '26", "Rotational Motion, Gravitation", "Thermodynamics, Equilibrium", "Coordinate Geometry: Lines, Circles"),
                PlannerRow("Aug '26", "Solids, Fluids, SHM, Waves", "Redox, GOC, Hydrocarbons", "Parabola, Ellipse, Hyperbola"),
                PlannerRow("Sep '26", "Electrostatics, Current Electricity", "Solutions, Electrochemistry", "Calculus: Limits, Differentiability, AOD"),
                PlannerRow("Oct '26", "Magnetism, EMI, AC", "Coordination, p-Block, d&f Block", "Integration: Definite & Indefinite"),
                PlannerRow("Nov '26", "Ray Optics, Wave Optics", "Haloalkanes, Alcohols, Aldehydes", "Vectors & 3D Geometry"),
                PlannerRow("Dec '26", "Modern Physics, Semiconductors", "Amines, Biomolecules", "Matrices, Determinants, Probability"),
                PlannerRow("Jan '27", "JEE Mains Focus: Revision + Mocks", "Inorganic Memorisation + Mocks", "Mock Tests"),
                PlannerRow("Feb '27", "Board Prep + Error Analysis", "Board Prep", "Board Prep")
            )
            PlanType.TWO_YEAR -> listOf(
                PlannerRow("May '26", "Units & Measurements", "Mole Concept", "Sets, Relations & Functions"),
                PlannerRow("Jun '26", "Motion in a Straight Line", "Structure of Atom", "Trigonometry"),
                PlannerRow("Jul '26", "Motion in a Plane", "Periodic Table", "Complex Nos & Quadratic Eq"),
                PlannerRow("Aug '26", "Laws of Motion", "Chemical Bonding", "Permutation & Combination"),
                PlannerRow("Sep '26", "Work, Energy & Power", "Thermodynamics", "Binomial, Sequence & Series"),
                PlannerRow("Oct '26", "Rotation", "Equilibrium", "Straight Lines"),
                PlannerRow("Nov '26", "Gravitation", "Redox Reactions", "Circles & Conic Sections"),
                PlannerRow("Dec '26", "Properties of Matter", "P-block", "Limits & Derivatives"),
                PlannerRow("Jan '27", "Thermal Properties, Thermo", "GOC (Organic Basics)", "Probability"),
                PlannerRow("Feb '27", "Oscillations, Waves", "Hydrocarbons", "Statistics"),
                PlannerRow("Mar '27", "Electrostatics", "Solutions", "Matrices & Determinants"),
                PlannerRow("Apr '27", "Capacitance", "Electrochemistry", "Continuity & Differentiability"),
                PlannerRow("May '27", "Current Electricity", "Chemical Kinetics", "Differentiation Methods"),
                PlannerRow("Jun '27", "Moving Charges & Magnetism", "d & f Block Elements", "Applications of Derivatives"),
                PlannerRow("Jul '27", "Magnetism, EMI", "Coordination Compounds", "Indefinite Integration"),
                PlannerRow("Aug '27", "Alternating Current", "Haloalkanes & Haloarenes", "Definite Integration"),
                PlannerRow("Sep '27", "Ray Optics", "Alcohols, Phenols & Ethers", "Differential Equations"),
                PlannerRow("Oct '27", "Wave Optics, Dual Nature", "Aldehydes, Ketones & Acids", "Vector Algebra"),
                PlannerRow("Nov '27", "Atoms, Nuclei, Semiconductor", "Amines, Biomolecules", "3D Geometry"),
                PlannerRow("Dec '27", "11th Revision", "11th Revision (Physical)", "Probability"),
                PlannerRow("Jan '28", "Full Syllabus Mock Tests", "Full Syllabus Mock Tests", "Full Syllabus Mock Tests")
            )
        }
        ExamType.NEET -> when (plan) {
            PlanType.ONE_YEAR -> listOf(
                PlannerRow("May '26", "Units, Motion Basics", "Mole Concept, Atomic Structure", "Living World, Bio Classification"),
                PlannerRow("Jun '26", "Laws of Motion, Work & Energy", "Chemical Bonding", "Morphology, Anatomy"),
                PlannerRow("Jul '26", "Rotational Motion, Gravitation", "Thermodynamics, Equilibrium", "Cell Biology, Biomolecules"),
                PlannerRow("Aug '26", "Mechanical & Thermal Properties", "Redox, s-Block, GOC", "Transport in Plants, Photosynthesis"),
                PlannerRow("Sep '26", "Electrostatics, Current Electricity", "Solutions, Electrochemistry", "Human Physio I: Digestion, Breathing"),
                PlannerRow("Oct '26", "Magnetism, EMI, AC", "Chemical Kinetics", "Human Physio II: Excretory, Neural"),
                PlannerRow("Nov '26", "Ray Optics, Wave Optics", "p-Block, d&f Block, Coordination", "Reproduction: Plants, Human Repro"),
                PlannerRow("Dec '26", "Modern Physics, Semiconductors", "Haloalkanes, Alcohols, Aldehydes", "Genetics, Molecular Basis, Evolution"),
                PlannerRow("Jan '27", "Revision: Mechanics & Electro", "Amines, Biomolecules, Polymers", "Health & Disease, Microbes, Biotech"),
                PlannerRow("Feb '27", "Final Mocks + PYQs", "Final Mocks", "Ecology + NCERT Line-by-Line")
            )
            PlanType.TWO_YEAR -> listOf(
                PlannerRow("May '26", "Units & Measurements", "Mole Concept", "Living World, Bio Classification"),
                PlannerRow("Jun '26", "Motion in a Straight Line", "Structure of Atom", "Plant Kingdom, Animal Kingdom"),
                PlannerRow("Jul '26", "Motion in a Plane", "Periodic Table", "Morphology & Anatomy"),
                PlannerRow("Aug '26", "Laws of Motion", "Chemical Bonding", "Cell: Unit of Life, Biomolecules"),
                PlannerRow("Sep '26", "Work, Energy & Power", "Thermodynamics", "Cell Cycle, Transport in Plants"),
                PlannerRow("Oct '26", "Rotation", "Equilibrium", "Photosynthesis, Respiration"),
                PlannerRow("Nov '26", "Gravitation", "Redox Reactions", "Plant Growth, Digestion"),
                PlannerRow("Dec '26", "Properties of Matter", "P-Block", "Breathing, Body Fluids"),
                PlannerRow("Jan '27", "Thermal Properties", "GOC (Organic Basics)", "Excretory, Locomotion"),
                PlannerRow("Feb '27", "Oscillations, Waves", "Hydrocarbons", "Neural Control, Chemical Coordination"),
                PlannerRow("Mar '27", "Electrostatics", "Solutions", "Reproduction in Organisms"),
                PlannerRow("Apr '27", "Capacitance", "Electrochemistry", "Human Reproduction"),
                PlannerRow("May '27", "Current Electricity", "Chemical Kinetics", "Reproductive Health"),
                PlannerRow("Jun '27", "Moving Charges & Magnetism", "d & f Block Elements", "Principles of Inheritance"),
                PlannerRow("Jul '27", "Magnetism, EMI", "Coordination Compounds", "Molecular Basis of Inheritance"),
                PlannerRow("Aug '27", "Alternating Current", "Haloalkanes & Haloarenes", "Evolution, Human Health"),
                PlannerRow("Sep '27", "Ray Optics", "Alcohols, Phenols & Ethers", "Microbes, Biotech Principles"),
                PlannerRow("Oct '27", "Wave Optics, Dual Nature", "Aldehydes, Ketones & Acids", "Biotech Applications"),
                PlannerRow("Nov '27", "Atoms, Nuclei, Semiconductor", "Amines, Biomolecules", "Ecosystem, Biodiversity"),
                PlannerRow("Dec '27", "11th Revision", "11th Revision", "11th Revision (Plant Physio)"),
                PlannerRow("Jan '28", "Full Syllabus Mock Tests", "Full Syllabus Mock Tests", "Full Syllabus Mock Tests")
            )
        }
    }

    fun getEssentialResources(exam: ExamType): List<Pair<String, String>> = listOf(
        "📔" to "Formula Notebook — Physics & ${exam.thirdSubject()} short notes",
        "❌" to "Mistake Notebook — Log every wrong test & homework question",
        "📘" to if (exam == ExamType.NEET) "NCERT Books — Biology & Chemistry line-by-line mastery" else "NCERT + Exemplar — Essential for Inorganic & Mains",
        "📁" to "PYQ Bank — Topic-wise 20-year JEE/NEET archive",
        "📝" to "Weekly Tests — Every Sunday full-length exam simulation"
    )

    fun getDailySchedule(exam: ExamType): List<Triple<String, String, Long>> = listOf(
        Triple("5–6 AM", "Morning Revision & Flashcards", 0xFFF5C518),
        Triple("6–8 AM", "Physics Deep Concept & Numericals", 0xFF06B6D4),
        Triple("9–11 AM", "Chemistry Mechanisms & NCERT", 0xFF8B5CF6),
        Triple("11–1 PM", exam.thirdSubject(), if (exam == ExamType.NEET) 0xFF10B981 else 0xFFF5C518),
        Triple("4–6 PM", "Tough Competitive Homework (PPT)", 0xFFF97316),
        Triple("7–9 PM", "Mindmaps & Answer PDF Review", 0xFFEC4899),
        Triple("9–10 PM", "Error Log + Improvement Practice", 0xFF2563EB)
    )
}
