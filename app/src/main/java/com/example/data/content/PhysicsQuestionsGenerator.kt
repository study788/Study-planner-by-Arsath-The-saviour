package com.example.data.content

import com.example.data.model.QuestionItem
import kotlin.math.sqrt

object PhysicsQuestionsGenerator {

    fun generate(exam: String, targetCount: Int = 1050): List<QuestionItem> {
        val chapters = SyllabusChapters.chaptersFor(exam, "Physics")
        val list = ArrayList<QuestionItem>(targetCount)

        // Curated high-yield foundational & competitive questions first
        val curated = curatedPhysics(exam, chapters)
        list.addAll(curated)

        var idx = list.size
        while (list.size < targetCount) {
            val chapter = chapters[idx % chapters.size]
            val templateIndex = idx % 18
            val variant = idx / 18 + 1
            val diff = QuestionBankHelper.difficultyForIndex(idx)
            val id = "${exam}_PHY_${(list.size + 1).toString().padStart(4, '0')}"
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
                val u = 4 + (v % 15) * 2
                val a = 2 + (v % 6)
                val time = 3 + (v % 7)
                val ans = u + a * time
                QuestionBankHelper.makeQuestion(
                    id, exam, "Physics", chapter, diff,
                    "[#$v] A particle starts with initial velocity $u m/s and uniform acceleration $a m/s². What is its velocity after $time s?",
                    "$ans m/s", "${ans + a} m/s", "${u * time} m/s", "${ans - 2} m/s",
                    "Using v = u + at: v = $u + ($a × $time) = $ans m/s.", seed
                )
            }
            1 -> {
                val u = 10 * (1 + (v % 8))
                val g = 10
                val h = (u * u) / (2 * g)
                QuestionBankHelper.makeQuestion(
                    id, exam, "Physics", chapter, diff,
                    "[#$v] A ball is projected vertically upward with speed $u m/s (g = 10 m/s²). Find the maximum height reached.",
                    "$h m", "${h * 2} m", "${h / 2} m", "${u * 2} m",
                    "Maximum height H = u² / (2g) = ($u²) / 20 = $h m.", seed
                )
            }
            2 -> {
                val m = 2 + (v % 10)
                val force = m * (3 + (v % 5)) + (v % 3) * m
                val mu = 0.2
                val acc = (force - mu * m * 10.0) / m
                val ans = QuestionBankHelper.round2(acc)
                QuestionBankHelper.makeQuestion(
                    id, exam, "Physics", chapter, diff,
                    "[#$v] A block of mass $m kg rests on a rough horizontal floor (μ = 0.2, g = 10 m/s²). A horizontal force of $force N is applied. Its acceleration is:",
                    "$ans m/s²", "${QuestionBankHelper.round2(force.toDouble() / m)} m/s²", "${QuestionBankHelper.round2(acc + 1.5)} m/s²", "${QuestionBankHelper.round2(acc / 2.0)} m/s²",
                    "Limiting friction f = μmg = 0.2 × $m × 10 = ${2 * m} N. Net a = (F − f)/m = ($force − ${2 * m})/$m = $ans m/s².", seed
                )
            }
            3 -> {
                val m = 2 + (v % 8)
                val vel = 3 + (v % 12)
                val ke = 0.5 * m * vel * vel
                val ans = QuestionBankHelper.round2(ke)
                QuestionBankHelper.makeQuestion(
                    id, exam, "Physics", chapter, diff,
                    "[#$v] A body of mass $m kg moves at $vel m/s. Find its kinetic energy.",
                    "$ans J", "${m * vel * vel} J", "${m * vel} J", "${QuestionBankHelper.round2(ke + 12)} J",
                    "Kinetic Energy K = ½mv² = 0.5 × $m × ($vel)² = $ans J.", seed
                )
            }
            4 -> {
                val m = 2 + (v % 7)
                val r = 1 + (v % 5)
                val moi = m * r * r
                QuestionBankHelper.makeQuestion(
                    id, exam, "Physics", chapter, diff,
                    "[#$v] A thin circular ring of mass $m kg and radius $r m rotates about its symmetry axis perpendicular to its plane. Moment of inertia is:",
                    "$moi kg·m²", "${QuestionBankHelper.round2(0.5 * moi)} kg·m²", "${QuestionBankHelper.round2(0.4 * moi)} kg·m²", "${moi * 2} kg·m²",
                    "For a ring about its central perpendicular axis, I = MR² = $m × $r² = $moi kg·m².", seed
                )
            }
            5 -> {
                val factor = 2 + (v % 5)
                val newG = QuestionBankHelper.round2(9.8 / (factor * factor))
                QuestionBankHelper.makeQuestion(
                    id, exam, "Physics", chapter, diff,
                    "[#$v] At what value does acceleration due to gravity stand at height h = ${(factor - 1)}R above Earth's surface (take surface g = 9.8 m/s²)?",
                    "$newG m/s²", "${QuestionBankHelper.round2(9.8 / factor)} m/s²", "${QuestionBankHelper.round2(9.8 * factor)} m/s²", "${QuestionBankHelper.round2(9.8 / (factor + 1))} m/s²",
                    "g' = g / (1 + h/R)² = 9.8 / ($factor)² = $newG m/s².", seed
                )
            }
            6 -> {
                val r1 = 2 + (v % 6)
                val r2 = 4 + (v % 8)
                val eq = QuestionBankHelper.round2((r1 * r2).toDouble() / (r1 + r2))
                QuestionBankHelper.makeQuestion(
                    id, exam, "Physics", chapter, diff,
                    "[#$v] Two resistors $r1 Ω and $r2 Ω are connected in parallel. The equivalent resistance is:",
                    "$eq Ω", "${r1 + r2} Ω", "${QuestionBankHelper.round2((r1 + r2) / 2.0)} Ω", "${r1 * r2} Ω",
                    "R_eq = (R₁R₂)/(R₁ + R₂) = (${r1 * r2})/(${r1 + r2}) = $eq Ω.", seed
                )
            }
            7 -> {
                val c1 = 3 + (v % 6)
                val c2 = 6 + (v % 6)
                val eq = QuestionBankHelper.round2((c1 * c2).toDouble() / (c1 + c2))
                QuestionBankHelper.makeQuestion(
                    id, exam, "Physics", chapter, diff,
                    "[#$v] Capacitors $c1 μF and $c2 μF are connected in series across a battery. Equivalent capacitance is:",
                    "$eq μF", "${c1 + c2} μF", "${QuestionBankHelper.round2((c1 + c2) / 2.0)} μF", "${c1 * c2} μF",
                    "In series: 1/C_eq = 1/$c1 + 1/$c2 => C_eq = ($c1×$c2)/($c1+$c2) = $eq μF.", seed
                )
            }
            8 -> {
                val voltage = 12 * (1 + (v % 5))
                val resistance = 2 + (v % 8)
                val power = QuestionBankHelper.round2((voltage * voltage).toDouble() / resistance)
                QuestionBankHelper.makeQuestion(
                    id, exam, "Physics", chapter, diff,
                    "[#$v] A $resistance Ω resistor is connected across a $voltage V ideal source. Power dissipated is:",
                    "$power W", "${voltage * resistance} W", "${QuestionBankHelper.round2(voltage.toDouble() / resistance)} W", "${QuestionBankHelper.round2((voltage * voltage).toDouble() / (resistance * 2))} W",
                    "Power P = V² / R = ($voltage)² / $resistance = $power W.", seed
                )
            }
            9 -> {
                val n = 2 + (v % 4)
                val energyEv = QuestionBankHelper.round2(-13.6 / (n * n))
                QuestionBankHelper.makeQuestion(
                    id, exam, "Physics", chapter, diff,
                    "[#$v] In a hydrogen atom, what is the total energy of an electron in the n = $n Bohr orbit?",
                    "$energyEv eV", "${QuestionBankHelper.round2(-13.6 / n)} eV", "${QuestionBankHelper.round2(13.6 / (n * n))} eV", "${QuestionBankHelper.round2(-27.2 / (n * n))} eV",
                    "Bohr orbit energy E_n = -13.6 / n² eV = -13.6 / ${n * n} = $energyEv eV.", seed
                )
            }
            10 -> {
                val halfLife = 5 + (v % 10)
                val elapsed = halfLife * (2 + (v % 4))
                val nHalves = elapsed / halfLife
                val denom = 1 shl nHalves
                QuestionBankHelper.makeQuestion(
                    id, exam, "Physics", chapter, diff,
                    "[#$v] A radioactive sample has half-life $halfLife days. What fraction of active nuclei remains after $elapsed days?",
                    "1/$denom", "1/${denom * 2}", "1/${nHalves * 2}", "${denom - 1}/$denom",
                    "Number of half-lives n = $elapsed / $halfLife = $nHalves. Remaining fraction = (1/2)^$nHalves = 1/$denom.", seed
                )
            }
            11 -> {
                val u = -(20 + (v % 5) * 10)
                val f = -(10 + (v % 3) * 5)
                val vImg = QuestionBankHelper.round2((f * u).toDouble() / (u - f))
                QuestionBankHelper.makeQuestion(
                    id, exam, "Physics", chapter, diff,
                    "[#$v] An object is placed at ${-u} cm in front of a concave mirror of focal length ${-f} cm. Image position v is:",
                    "$vImg cm", "${-f * 2} cm", "${QuestionBankHelper.round2((u + f).toDouble())} cm", "${-u / 2} cm",
                    "Mirror formula: 1/v + 1/u = 1/f => v = (fu)/(u - f) = $vImg cm.", seed
                )
            }
            12 -> {
                val xl = 30 + (v % 6) * 10
                val xc = 10 + (v % 4) * 5
                val r = 15 + (v % 5) * 5
                val diffX = xl - xc
                val z = QuestionBankHelper.round2(sqrt((r * r + diffX * diffX).toDouble()))
                QuestionBankHelper.makeQuestion(
                    id, exam, "Physics", chapter, diff,
                    "[#$v] In a series LCR circuit, R = $r Ω, X_L = $xl Ω, and X_C = $xc Ω. Find the total impedance Z.",
                    "$z Ω", "${r + xl + xc} Ω", "${r + diffX} Ω", "${QuestionBankHelper.round2((r + xl - xc) / 2.0)} Ω",
                    "Impedance Z = √(R² + (X_L − X_C)²) = √(${r}² + ${diffX}²) = $z Ω.", seed
                )
            }
            13 -> {
                val m = 1 + (v % 6)
                val k = 100 * (1 + (v % 5))
                val omega = QuestionBankHelper.round2(sqrt(k.toDouble() / m))
                QuestionBankHelper.makeQuestion(
                    id, exam, "Physics", chapter, diff,
                    "[#$v] A block of mass $m kg attached to a spring of force constant $k N/m executes SHM. Its angular frequency ω is:",
                    "$omega rad/s", "${k / m} rad/s", "${QuestionBankHelper.round2(sqrt(m.toDouble() / k))} rad/s", "${QuestionBankHelper.round2(2.0 * k / m)} rad/s",
                    "ω = √(k/m) = √($k/$m) = $omega rad/s.", seed
                )
            }
            14 -> {
                val tCold = 250 + (v % 5) * 20
                val tHot = 500 + (v % 6) * 50
                val eff = QuestionBankHelper.round2((1.0 - tCold.toDouble() / tHot) * 100.0)
                QuestionBankHelper.makeQuestion(
                    id, exam, "Physics", chapter, diff,
                    "[#$v] A Carnot heat engine operates between source $tHot K and sink $tCold K. Its maximum efficiency is:",
                    "$eff%", "${QuestionBankHelper.round2(tCold.toDouble() / tHot * 100)}%", "${QuestionBankHelper.round2((tHot - tCold).toDouble() / 10.0)}%", "100%",
                    "Carnot efficiency η = (1 − T_c / T_h) × 100% = (1 − $tCold/$tHot) × 100% = $eff%.", seed
                )
            }
            15 -> {
                val q = 2 + (v % 7)
                val vel = 3 + (v % 5)
                val b = 4 + (v % 6)
                val force = q * vel * b
                QuestionBankHelper.makeQuestion(
                    id, exam, "Physics", chapter, diff,
                    "[#$v] A charge $q μC enters perpendicular to a uniform magnetic field $b T at $vel × 10⁵ m/s. Magnetic force magnitude is:",
                    "${QuestionBankHelper.round2(force * 0.1)} N", "${force} N", "${QuestionBankHelper.round2(force * 0.01)} N", "${q * b} N",
                    "F = qvB sin90° = ($q × 10⁻⁶) × ($vel × 10⁵) × $b = ${QuestionBankHelper.round2(force * 0.1)} N.", seed
                )
            }
            16 -> {
                val lam = 400 + (v % 6) * 50
                val kMax = QuestionBankHelper.round2(1240.0 / lam - 2.0)
                QuestionBankHelper.makeQuestion(
                    id, exam, "Physics", chapter, diff,
                    "[#$v] Light of wavelength $lam nm strikes a metal of work function 2.0 eV (hc ≈ 1240 eV·nm). Maximum kinetic energy of photoelectrons is:",
                    "$kMax eV", "${QuestionBankHelper.round2(1240.0 / lam + 2.0)} eV", "${QuestionBankHelper.round2(1240.0 / lam)} eV", "0.25 eV",
                    "K_max = hc/λ − φ = 1240/$lam − 2.0 = $kMax eV.", seed
                )
            }
            else -> {
                val q1 = 2 + (v % 6)
                val r = 1 + (v % 4)
                val field = QuestionBankHelper.round2(9.0 * q1 / (r * r))
                QuestionBankHelper.makeQuestion(
                    id, exam, "Physics", chapter, diff,
                    "[#$v] Find the electric field magnitude at distance $r m from a point charge $q1 μC in vacuum (k = 9 × 10⁹ N·m²/C²):",
                    "$field × 10³ N/C", "${QuestionBankHelper.round2(9.0 * q1 / r)} × 10³ N/C", "${field} × 10⁶ N/C", "${q1 * r} × 10³ N/C",
                    "E = kq/r² = (9 × 10⁹ × $q1 × 10⁻⁶) / $r² = $field × 10³ N/C.", seed
                )
            }
        }
    }

    private fun curatedPhysics(exam: String, chapters: List<String>): List<QuestionItem> = listOf(
        QuestionBankHelper.makeQuestion(
            "${exam}_PHY_0001", exam, "Physics", chapters[1 % chapters.size], "Easy",
            "A particle starts from rest with uniform acceleration a. Velocity after time t is:",
            "v = at", "v = at²", "v = ½at²", "v = 2at",
            "From first equation of motion v = u + at; with u = 0, v = at.", 1
        ),
        QuestionBankHelper.makeQuestion(
            "${exam}_PHY_0002", exam, "Physics", chapters[2 % chapters.size], "Easy",
            "Newton's second law directly relates net external force with:",
            "Rate of change of momentum / Acceleration", "Displacement", "Potential Energy", "Terminal Velocity",
            "F_net = dp/dt = ma for constant mass systems.", 2
        ),
        QuestionBankHelper.makeQuestion(
            "${exam}_PHY_0003", exam, "Physics", chapters[3 % chapters.size], "Medium",
            "Work done by centripetal force on a body in uniform circular motion is always:",
            "Zero", "Positive", "Negative", "Depends on radius",
            "Centripetal force is perpendicular to instantaneous displacement (cos 90° = 0).", 3
        ),
        QuestionBankHelper.makeQuestion(
            "${exam}_PHY_0004", exam, "Physics", chapters[9 % chapters.size], "Easy",
            "The SI unit of electric charge is:",
            "Coulomb", "Ampere", "Volt", "Tesla",
            "Electric charge Q = I·t is measured in Coulombs (C).", 4
        ),
        QuestionBankHelper.makeQuestion(
            "${exam}_PHY_0005", exam, "Physics", chapters[11 % chapters.size], "Medium",
            "Power in an electrical DC resistor can be written as:",
            "P = V² / R = I²R", "P = IR", "P = V / R", "P = I / V",
            "Electrical power P = VI = I²R = V²/R.", 5
        )
    )
}
