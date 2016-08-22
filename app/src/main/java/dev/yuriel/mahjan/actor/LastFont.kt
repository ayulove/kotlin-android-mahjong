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

package dev.yuriel.mahjan.actor

import com.badlogic.gdx.scenes.scene2d.ui.Label
import dev.yuriel.mahjan.texture.NormalFontBlock

/**
 * Created by yuriel on 8/18/16.
 */
class LastFont {

    var font: NormalFontBlock?
        private set

    var text: String = ""
        set(value) {
            field = value
            font?.text = value
        }

    val actor: Label?
        get() = font?.label

    init {
        font = NormalFontBlock()
        font!!.load("last.fnt", "last.png")
        actor?.setFontScale(0.7F)
    }

    fun update(num: Int) {
        text = "残り $num"
    }
}