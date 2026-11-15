package com.example.data.content

import com.example.data.model.QuestionItem
import kotlin.math.sqrt

object MathQuestionsGenerator {

    fun generate(targetCount: Int = 1050): List<QuestionItem> {
        val chapters = SyllabusChapters.jeeMathematics
        val list = ArrayList<QuestionItem>(targetCount)
        list.addAll(curatedMath(chapters))

        var idx = list.size
        while (list.size < targetCount) {
            val chapter = chapters[idx % chapters.size]
            val templateIndex = idx % 16
            val variant = idx / 16 + 1
            val diff = QuestionBankHelper.difficultyForIndex(idx)
            val id = "JEE_MAT_${(list.size + 1).toString().padStart(4, '0')}"
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
            0 -> {
                val r1 = 1 + (v % 9)
                val r2 = 2 + (v % 11)
                val sum = r1 + r2
                val prod = r1 * r2
                QuestionBankHelper.makeQuestion(
                    id, "JEE", "Mathematics", chapter, diff,
                    "[#$v] If α and β are the roots of x² − ${sum}x + $prod = 0, find the value of α² + β².",
                    "${sum * sum - 2 * prod}", "${sum * sum}", "${sum * sum + 2 * prod}", "${prod * 2}",
                    "α + β = $sum and αβ = $prod. Therefore α² + β² = (α + β)² − 2αβ = ${sum * sum} − ${2 * prod} = ${sum * sum - 2 * prod}.", seed
                )
            }
            1 -> {
                val a = 2 + (v % 7)
                val b = 3 + (v % 8)
                val modulus = QuestionBankHelper.round2(sqrt((a * a + b * b).toDouble()))
                QuestionBankHelper.makeQuestion(
                    id, "JEE", "Mathematics", chapter, diff,
                    "[#$v] Find the modulus |z| of the complex number z = $a + ${b}i.",
                    modulus, "${a + b}", "${a * a + b * b}", "${kotlin.math.abs(a - b)}",
                    "|a + ib| = √(a² + b²) = √(${a}² + ${b}²) = √(${a * a + b * b}) = $modulus.", seed
                )
            }
            2 -> {
                val a = 3 + (v % 6)
                val d = 2 + (v % 5)
                val n = 6 + (v % 6)
                val sum = n * (2 * a + (n - 1) * d) / 2
                QuestionBankHelper.makeQuestion(
                    id, "JEE", "Mathematics", chapter, diff,
                    "[#$v] In an arithmetic progression with first term a = $a and common difference d = $d, find the sum of the first $n terms S_$n.",
                    "$sum", "${sum + n}", "${sum - d}", "${a + (n - 1) * d}",
                    "S_n = (n/2)[2a + (n − 1)d] = ($n/2)[${2 * a} + ${(n - 1) * d}] = $sum.", seed
                )
            }
            3 -> {
                val n = 6 + (v % 6)
                val r = 2 + (v % 3)
                var num = 1L
                var den = 1L
                for (i in 0 until r) {
                    num *= (n - i)
                    den *= (i + 1)
                }
                val ncr = num / den
                QuestionBankHelper.makeQuestion(
                    id, "JEE", "Mathematics", chapter, diff,
                    "[#$v] Evaluate the binomial coefficient ^${n}C_${r}.",
                    "$ncr", "${ncr * r}", "${n * r}", "${ncr + n}",
                    "^nC_r = n! / (r!(n − r)!) => ^${n}C_${r} = $ncr.", seed
                )
            }
            4 -> {
                val coeffA = 2 + (v % 5)
                val coeffB = 3 + (v % 6)
                val coeffC = 5 + (v % 7)
                val prodTerm = coeffA * coeffA
                val denom = coeffA * coeffB
                QuestionBankHelper.makeQuestion(
                    id, "JEE", "Mathematics", chapter, diff,
                    "[#$v] Find the derivative f'(x) at x = 1 for f(x) = ${coeffA}x³ + ${coeffB}x² − ${coeffC}x + 9.",
                    "${3 * coeffA + 2 * coeffB - coeffC}", "${coeffA + coeffB - coeffC}", "${6 * coeffA + 2 * coeffB}", "${3 * coeffA + coeffB}",
                    "f'(x) = ${3 * coeffA}x² + ${2 * coeffB}x − $coeffC. At x = 1, f'(1) = ${3 * coeffA + 2 * coeffB - coeffC}.", seed
                )
            }
            5 -> {
                val k = 2 + (v % 8)
                val upper = 1 + (v % 4)
                val valInt = k * (upper * upper * upper) / 3.0
                val ans = QuestionBankHelper.round2(valInt)
                QuestionBankHelper.makeQuestion(
                    id, "JEE", "Mathematics", chapter, diff,
                    "[#$v] Evaluate the definite integral ∫₀^$upper ${k}x² dx.",
                    ans, "${QuestionBankHelper.round2((k * upper * upper).toDouble())}", "${QuestionBankHelper.round2(valInt * 2)}", "${k * upper}",
                    "∫ ${k}x² dx = (${k}/3)x³. Evaluating from 0 to $upper gives (${k}/3)×${upper * upper * upper} = $ans.", seed
                )
            }
            6 -> {
                val k = 1 + (v % 6)
                val deg = 3 + (v % 4)
                val limitVal = deg * Math.pow(k.toDouble(), (deg - 1).toDouble()).toLong()
                QuestionBankHelper.makeQuestion(
                    id, "JEE", "Mathematics", chapter, diff,
                    "[#$v] Compute lim(x → $k) (x^$deg − ${k}^$deg) / (x − $k).",
                    "$limitVal", "${limitVal / deg}", "${deg * k}", "${limitVal + k}",
                    "By standard limit lim(x→a) (x^n − a^n)/(x − a) = n·a^(n−1) = $deg × ${k}^${deg - 1} = $limitVal.", seed
                )
            }
            7 -> {
                val a11 = 2 + (v % 5)
                val a12 = 1 + (v % 4)
                val a21 = 3 + (v % 4)
                val a22 = 4 + (v % 6)
                val det = a11 * a22 - a12 * a21
                QuestionBankHelper.makeQuestion(
                    id, "JEE", "Mathematics", chapter, diff,
                    "[#$v] Find the determinant of 2×2 matrix [[${a11}, ${a12}], [${a21}, ${a22}]].",
                    "$det", "${a11 * a22 + a12 * a21}", "${a12 * a21 - a11 * a22}", "${a11 + a22}",
                    "det(A) = ($a11 × $a22) − ($a12 × $a21) = ${a11 * a22} − ${a12 * a21} = $det.", seed
                )
            }
            8 -> {
                val x1 = 1 + (v % 4)
                val y1 = 2 + (v % 5)
                val z1 = -(1 + (v % 3))
                val x2 = 2 + (v % 3)
                val y2 = -(1 + (v % 4))
                val z2 = 3 + (v % 4)
                val dot = x1 * x2 + y1 * y2 + z1 * z2
                QuestionBankHelper.makeQuestion(
                    id, "JEE", "Mathematics", chapter, diff,
                    "[#$v] Compute the scalar dot product a · b where a = ${x1}i + ${y1}j + (${z1})k and b = ${x2}i + (${y2})j + ${z2}k.",
                    "$dot", "${dot + 4}", "${kotlin.math.abs(dot) + 3}", "${x1 * x2 + y1 * y2}",
                    "a · b = ($x1)($x2) + ($y1)($y2) + ($z1)($z2) = $dot.", seed
                )
            }
            9 -> {
                val a = 2 + (v % 5) // vertex coeff in y^2 = 4ax
                val focalLength = a
                val latus = 4 * a
                QuestionBankHelper.makeQuestion(
                    id, "JEE", "Mathematics", chapter, diff,
                    "[#$v] For the parabola y² = ${4 * a}x, find the length of the latus rectum and focus coordinates.",
                    "Latus rectum = $latus, Focus = ($focalLength, 0)",
                    "Latus rectum = ${2 * a}, Focus = (${2 * a}, 0)",
                    "Latus rectum = $a, Focus = (0, $focalLength)",
                    "Latus rectum = ${8 * a}, Focus = (-$focalLength, 0)",
                    "Comparing y² = ${4 * a}x with standard y² = 4ax gives a = $a. Focus is ($a, 0) and latus rectum = 4a = $latus.", seed
                )
            }
            10 -> {
                val h = 1 + (v % 6)
                val k = -(2 + (v % 5))
                val r = 3 + (v % 7)
                QuestionBankHelper.makeQuestion(
                    id, "JEE", "Mathematics", chapter, diff,
                    "[#$v] Find the center and radius of the circle (x − $h)² + (y + ${-k})² = ${r * r}.",
                    "Center ($h, $k), Radius = $r",
                    "Center (-$h, ${-k}), Radius = ${r * r}",
                    "Center ($h, ${-k}), Radius = $r",
                    "Center ($h, $k), Radius = ${r * r}",
                    "Comparing with (x − h)² + (y − k)² = r² gives Center = ($h, $k) and radius r = √${r * r} = $r.", seed
                )
            }
            11 -> {
                val pA = QuestionBankHelper.round2(0.3 + (v % 4) * 0.1)
                val pB = QuestionBankHelper.round2(0.2 + (v % 3) * 0.15)
                val aVal = pA.toDouble()
                val bVal = pB.toDouble()
                val union = QuestionBankHelper.round2(aVal + bVal - aVal * bVal)
                QuestionBankHelper.makeQuestion(
                    id, "JEE", "Mathematics", chapter, diff,
                    "[#$v] A and B are independent events with P(A) = $pA and P(B) = $pB. Calculate P(A ∪ B).",
                    union, QuestionBankHelper.round2(aVal + bVal), QuestionBankHelper.round2(aVal * bVal), "0.99",
                    "Since independent, P(A ∩ B) = P(A)P(B). Thus P(A ∪ B) = P(A) + P(B) − P(A)P(B) = $union.", seed
                )
            }
            12 -> {
                val slope = 2 + (v % 6)
                val x0 = 1 + (v % 4)
                val y0 = 3 + (v % 5)
                val c = y0 - slope * x0
                val cStr = if (c >= 0) "+ $c" else "− ${-c}"
                QuestionBankHelper.makeQuestion(
                    id, "JEE", "Mathematics", chapter, diff,
                    "[#$v] Find the equation of the straight line passing through ($x0, $y0) with slope m = $slope.",
                    "y = ${slope}x $cStr",
                    "y = ${slope}x + ${y0 + slope * x0}",
                    "y = ${-slope}x + $y0",
                    "y = ${slope}x + $x0",
                    "Point-slope form: y − $y0 = $slope(x − $x0) => y = ${slope}x $cStr.", seed
                )
            }
            13 -> {
                val angle = listOf(0, 30, 45, 60, 90)[v % 5]
                val expr = when (angle) {
                    0 -> "sin²(0°) + cos(0°)"
                    30 -> "2 sin(30°) cos(30°)"
                    45 -> "tan²(45°) + sec²(45°)"
                    60 -> "cos²(60°) + sin(30°)"
                    else -> "sin(90°) + 2 cos(60°)"
                }
                val ans = when (angle) {
                    0 -> "1"
                    30 -> "√3 / 2"
                    45 -> "3"
                    60 -> "3/4"
                    else -> "2"
                }
                val wrongPool = listOf("0", "1/2", "4", "√2 / 2", "5/4").filter { it != ans }
                QuestionBankHelper.makeQuestion(
                    id, "JEE", "Mathematics", chapter, diff,
                    "[#$v] Evaluate the exact trigonometric value of $expr.",
                    ans, wrongPool[0], wrongPool[1], wrongPool[2],
                    "Substituting standard trigonometric ratios simplifies $expr directly to $ans.", seed
                )
            }
            14 -> {
                val nElem = 3 + (v % 5)
                val subsets = 1 shl nElem
                val proper = subsets - 1
                QuestionBankHelper.makeQuestion(
                    id, "JEE", "Mathematics", chapter, diff,
                    "[#$v] A finite set A has $nElem elements. How many non-empty subsets does A possess?",
                    "$proper", "$subsets", "${nElem * nElem}", "${subsets - 2}",
                    "Total subsets = 2^$nElem = $subsets. Excluding the empty set ∅ leaves 2^$nElem − 1 = $proper non-empty subsets.", seed
                )
            }
            else -> {
                val k = 2 + (v % 5)
                QuestionBankHelper.makeQuestion(
                    id, "JEE", "Mathematics", chapter, diff,
                    "[#$v] What are the order and degree of the differential equation (d²y/dx²)³ + ${k}(dy/dx)⁴ + y = 0?",
                    "Order = 2, Degree = 3",
                    "Order = 4, Degree = 2",
                    "Order = 3, Degree = 2",
                    "Order = 2, Degree = 4",
                    "Highest derivative present is d²y/dx² (order 2), and its highest power as a polynomial in derivatives is 3.", seed
                )
            }
        }
    }

    private fun curatedMath(chapters: List<String>): List<QuestionItem> = listOf(
        QuestionBankHelper.makeQuestion(
            "JEE_MAT_0001", "JEE", "Mathematics", chapters[7 % chapters.size], "Easy",
            "The value of lim(x → 0) (sin x / x) is:",
            "1", "0", "∞", "Does not exist",
            "Standard trigonometric limit: lim(x → 0) sin(x)/x = 1 (x in radians).", 201
        ),
        QuestionBankHelper.makeQuestion(
            "JEE_MAT_0002", "JEE", "Mathematics", chapters[9 % chapters.size], "Medium",
            "∫₀^(π/2) sin x / (sin x + cos x) dx equals:",
            "π / 4", "π / 2", "1", "0",
            "Using King's property ∫₀^a f(x)dx = ∫₀^a f(a−x)dx and adding gives 2I = π/2 => I = π/4.", 202
        ),
        QuestionBankHelper.makeQuestion(
            "JEE_MAT_0003", "JEE", "Mathematics", chapters[11 % chapters.size], "Medium",
            "For any invertible n×n matrix A, det(adj A) equals:",
            "(det A)^(n−1)", "(det A)^n", "1 / det A", "n · det A",
            "Since A · adj(A) = (det A) I_n, taking determinants gives det(A)·det(adj A) = (det A)^n.", 203
        ),
        QuestionBankHelper.makeQuestion(
            "JEE_MAT_0004", "JEE", "Mathematics", chapters[12 % chapters.size], "Tough",
            "If |a × b|² + |a · b|² = 144 and |a| = 4, what is |b|?",
            "3", "4", "6", "12",
            "By Lagrange's identity, |a × b|² + |a · b|² = |a|²|b|² => 16|b|² = 144 => |b| = 3.", 204
        ),
        QuestionBankHelper.makeQuestion(
            "JEE_MAT_0005", "JEE", "Mathematics", chapters[1 % chapters.size], "Tough",
            "If ω is a non-real cube root of unity, then 1 + ω + ω² equals:",
            "0", "1", "-1", "3",
            "Since ω³ − 1 = (ω − 1)(1 + ω + ω²) = 0 and ω ≠ 1, 1 + ω + ω² = 0.", 205
        )
    )
}
