/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 yuriel<yuriel3183@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.yuriel.kotmahjan.ctrl

import dev.yuriel.kotmahjan.ai.analysis.*
import dev.yuriel.kotmahjan.models.*
import dev.yuriel.kotmahjan.models.collections.Tehai
import org.junit.Test

/**
 * Created by yuriel on 7/28/16.
 */
class AnalysisTest {

    @Test
    fun testShantensu() {
        val tehai = Tehai.fromString("m6,m8,p6,p7,p8,p8,p8,p9,s3,s8,s8,s9,N")
        println(calculateShantensu(tehai.haiList, listOf()))
    }

    @Test
    fun testDistance() {
        val mgr = HaiMgr()
        while (mgr.hasHai()) {
            val h1 = mgr.getHai()
            val h2 = mgr.getHai()
            val (d, k1, k2) = distance(h1.id, h2.id)
            val result = "distance = $d, h1 = $h1, h2 = $h2, k1 = ${if (k1 != -1) Hai.newInstance(k1) else k1}, k2 = ${if (k2 != -1) Hai.newInstance(k2) else -1}"
            println(result)
            assert(d > -2 && d < 3) { result }
            if (d == 0) assert(h1 sameAs h2)
        }
    }

    @Test
    fun testExcludeShuntsu() {
        val a = intArrayOf(
                0, 0, 0, 1, 1, 1, 0, 1, 1,
                1, 1, 0, 1, 1, 1, 0, 0, 0,
                0, 0, 0, 0, 0, 4, 5, 6, 2,
                1, 1, 1, 0, 0, 0, 0)
        val b = excludeShuntsu(a)
        val c = intArrayOf(
                0, 0, 0, 0, 0, 0, 0, 1, 1,
                1, 1, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 1, 1,
                1, 1, 1, 0, 0, 0, 0)
        for(i in 0..b.size - 1) {
            print("${b[i]}, ")
            assert(b[i] == c [i])
        }
    }

    @Test
    fun testExcludeDoitsu() {
        val a = intArrayOf(
                3, 0, 0, 4, 1, 2, 0, 1, 1,
                1, 1, 0, 1, 1, 1, 0, 1, 3,
                0, 0, 0, 0, 0, 4, 3, 2, 2,
                2, 1, 2, 0, 0, 4, 0)
        val b = excludeKotsu(a)
        val c = intArrayOf(
                0, 0, 0, 1, 1, 2, 0, 1, 1,
                1, 1, 0, 1, 1, 1, 0, 1, 0,
                0, 0, 0, 0, 0, 1, 0, 2, 2,
                2, 1, 2, 0, 0, 1, 0)
        for(i in 0..b.size - 1) {
            print("${b[i]}, ")
            assert(b[i] == c [i])
        }
    }

    @Test
    fun testExcludeCorrelation() {
        val a = intArrayOf(
                3, 0, 0, 4, 1, 2, 0, 1, 1,
                1, 1, 0, 1, 1, 1, 0, 1, 3,
                0, 0, 0, 0, 0, 4, 3, 2, 2,
                2, 1, 2, 0, 0, 4, 0)
        val b = exclude2Correlation(a)
        val c = intArrayOf(
                1, 0, 0, 1, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 1,
                0, 0, 0, 0, 0, 0, 0, 0, 1,
                0, 1, 0, 0, 0, 0, 0)
        for (i in 0..b.useless.size - 1) {
            print("${b.keys[i]}, ")
        }
        println()
        for (i in 0..b.useless.size - 1) {
            print("${b.useless[i]}, ")
            assert(b.useless[i] == c [i])
        }
    }

    @Test
    fun testGeiKey() {
        val a = intArrayOf(
                3, 0, 0, 4, 1, 2, 0, 1, 1,
                1, 1, 0, 1, 1, 1, 0, 1, 3,
                0, 0, 0, 0, 0, 4, 3, 2, 2,
                2, 1, 2, 0, 0, 4, 0)
        val b = getUselessGeneralized(a)
        val c = intArrayOf(
                0, 0, 0, 0, 0, 0, 0, 0, 1,
                0, 0, 0, 0, 0, 0, 0, 1, 0,
                0, 0, 0, 0, 0, 1, 0, 0, 0,
                0, 1, 0, 0, 0, 1, 0
        )
        val d = intArrayOf(
                0, 0, 0, 0, 0, 0, 0, 0, 1,
                0, 0, 0, 0, 0, 0, 0, 1, 0,
                0, 0, 0, 0, 0, 0, 1, 0, 0,
                0, 1, 0, 0, 0, 1, 0)
        for (i in 0..b.useless.size - 1) {
            print("${b.keys[i]}, ")
        }
        println()
        for (i in 0..b.useless.size - 1) {
            print("${b.useless[i]}, ")
            assert(b.useless[i] == c [i] || b.useless[i] == d [i])
        }
    }

    @Test
    fun daHai() {
        var loop = 0
        //for (loop in 0..100) {
        loop@while(true) {
            loop ++
            println("loop@$loop")
            val tehai = Tehai()
            val mgr = HaiMgr()
            for (i in 0..2) {
                tehai.put(mgr.haiPai())
            }
            tehai.put(mgr.getHai())
            tehai.put(mgr.getHai())
            tehai.sort()
            println(tehai)

            var u = getUselessGeneralized(tehai.toTypedArray(false))
            var result = printResultByGen(u, tehai, false)
            if (result.first != 0) {
                println("da: ${Hai.newInstance(result.first)}")
            } else {
                u = getUselessSpecialized(tehai.toTypedArray(false))
                result = printResultByGen(u, tehai, true)
            }
            if (result.first != 0) {
                println("da: ${Hai.newInstance(result.first)}")
            } else {
                val b = sortEffectInRange(u, tehai.toTypedArray())
                println("extreme da: ${Hai.newInstance(b.g2kList[0].group[0])}")
                break@loop
            }

            for ((e, id) in result.second) {
                println("hai: ${Hai.newInstance(id)}, efficiencyByHand: ${e.efficiency}")
                for (h in e.keys) {
                    println("   >${Hai.newInstance(h)}, ")
                }
            }
            println()
        }
        println("total loop: $loop")
    }

    private fun printResultByGen(u: Useless2Key2KeyMap, tehai: Tehai, hl: Boolean = false):
            Pair<Int, List<Pair<Efficiency2Key, Int>>> {
        print("${if (hl) ANSI_YELLOW else ANSI_CYAN}")
        print("sute: ")
        for (i in 0..u.useless.size - 1) {
            //print("${u.first[i]}, ")
            if (0 == u.useless[i]) continue
            print("${Hai.newInstance(i + 1)},")
        }
        println()
        println("want: ")
        for (i in 0..u.keys.size - 1) {
            if (0 == u.keys[i]) continue
            print("   >${Hai.newInstance(i + 1)}, ")
            print("Because: ")
            for (b in u.k2gMap[i + 1]!!) {
                print("${Hai.newInstance(b)},")
            }
            println()
        }
        println()

        var resultId = 0
        var efficiency = Efficiency2Key()
        val list = mutableListOf<Pair<Efficiency2Key, Int>>()
        for (i in 0..u.useless.size - 1) {
            if (u.useless[i] < 1) continue
            val e = efficiencyByHand(tehai.toTypedArray(false), i + 1)
            list.add(Pair(e, i + 1))
            if (e.efficiency > efficiency.efficiency) {
                efficiency = e
                resultId = i + 1
            }
        }
        print(ANSI_RESET)
        return Pair(resultId, list)
    }
}

