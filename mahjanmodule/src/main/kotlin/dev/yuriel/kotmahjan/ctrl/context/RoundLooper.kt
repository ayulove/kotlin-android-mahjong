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

package dev.yuriel.kotmahjan.ctrl.context

import dev.yuriel.kotmahjan.models.*
import java.util.*
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.timer
import kotlin.jvm.internal.InlineMarker
import kotlin.properties.Delegates

/**
 * Created by yuriel on 7/17/16.
 */

@Deprecated("")
class RoundLooper(val rounder: RoundContextV1) {
    private var log: RoundEvent by Delegates.observable(RoundEvent()) {
        prop, old, new -> rounder.onReceiveEvent(new)
    }

    /**
     * 流れ
     */
    fun loop() {
        while (rounder.hasNextRound()) {
            //rounder.onStart()
            rounder.onHaiPai()
            var isFirstLoop = true
            looper@while (/*!rounder.isEndOfRound()*/false) {
                for (player in rounder.getPlayerList()) {
                    val looper = rounder.getLoopContext(player)
                    rounder.onLoop(player, looper)
                    looper.onStart()
                    looper.tsumo()
                    if (isFirstLoop && looper.is9x9Hai()) {
                        // todo
                        break@looper
                    }
                    while (looper.couldKan()) {
                        waitForKan(looper)
                        looper.tsumoAfterKan()
                    }
                    // todo 流局
                    looper.sute()
                    val actionPlayers = rounder.getPlayerList().startOf(player)
                    val listeners: MutableList<LoopContext> = mutableListOf()
                    for (p in actionPlayers) {
                        if (p == player) continue
                        val l = rounder.getLoopContext(p)
                        if (l.hasAction()) {
                            l.waitForAction()
                            listeners.add(l)
                        }
                    }
                    if (processAction(listeners)) break@looper
                    if (/*rounder.isEndOfRound()*/false) break@looper
                }
                isFirstLoop = false
            }
            rounder.onStop()
        }
        rounder.onEnd()
    }

    private fun waitForKan(looper: LoopContext, duration: Long = 5000, period: Long = 500) {
        val latch: CountDownLatch = CountDownLatch(1)
        var time: Long = 0
        timer("wait_for_kan", period = period) task@ {
            time += period
            if (ACTION_KAN == looper.getAction().action) {
                looper.onKan()
                this.cancel()
                latch.countDown()
            } else if (ACTION_CANCEL == looper.getAction().action || time > duration) {
                this.cancel()
                latch.countDown()
            }
        }
        latch.await()
    }

    // todo 銃杠
    private fun processAction(listener: List<LoopContext>, duration: Long = 5000, period: Long = 500): Boolean {
        val latch: CountDownLatch = CountDownLatch(1)
        var result: Boolean = false
        var time: Long = 0

        val rankingList: MutableMap<Int, MutableList<LoopContext>> = mutableMapOf(
                Pair(0, mutableListOf()),
                Pair(1, mutableListOf()),
                Pair(2, mutableListOf())
        )
        for (l in listener) {
            if (l.couldRon()) rankingList[0]?.add(l)
            if (l.couldPon() || l.couldKan()) rankingList[1]?.add(l)
            if (l.couldChi()) rankingList[2]?.add(l)
        }

        timer(name = "process_action", period = period) task@ {
            time += period
            for (l in listener) {
                if (ACTION_CANCEL == l.getAction().action) {
                    for ((index, list) in rankingList) {
                        list.remove(l)
                    }
                }
            }
            for (l in rankingList[0]!!) {
                if (ACTION_RON == l.getAction().action) {
                    result = true
                    l.onRon()
                }
            }
            if (result) {
                this.cancel()
                latch.countDown()
                return@task
            }
            if (rankingList[0]!!.isEmpty()) {
                for (l in rankingList[1]!!) {
                    if (ACTION_PON == l.getAction().action) {
                        l.onPon()
                        this.cancel()
                        latch.countDown()
                        return@task
                    } else if (ACTION_KAN == l.getAction().action) {
                        l.onKan()
                        this.cancel()
                        latch.countDown()
                        return@task
                    } else if (ACTION_CANCEL == l.getAction().action) {
                        rankingList[1]?.remove(l)
                    }
                }
            }
            if (rankingList[0]!!.isEmpty() && rankingList[1]!!.isEmpty()) {
                for (l in rankingList[2]!!) {
                    if (ACTION_CANCEL == l.getAction().action) {
                        rankingList[2]?.remove(l)
                    }
                    if (ACTION_CHI == rankingList[2]!![0].getAction().action) {
                        rankingList[2]!![0].onChi()
                        this.cancel()
                        latch.countDown()
                        return@task
                    }
                }
            }

            if (time > duration) {
                this.cancel()
                latch.countDown()
                return@task
            }
        }
        latch.await()
        return result
    }

    fun <T> List<T>.startOf(t: T): List<T> {
        if (t !in this) return listOf()
        val result: MutableList<T> = mutableListOf()
        val startAt = indexOf(t)
        for (i in 0..size - 1) {
            if (i + startAt < size)
                result.add(this[i + startAt])
            else
                result.add(this[i - startAt])
        }
        return result
    }
}