/*
 * Copyright (C) 2016. Yuriel - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package dev.yuriel.mahjan.model

import dev.yuriel.kotmahjan.models.*
import dev.yuriel.kotmahjan.models.collections.Kawa
import dev.yuriel.kotmahjan.models.collections.Mentsu
import dev.yuriel.kotmahjan.models.collections.Tehai

/**
 * Created by yuriel on 8/13/16.
 */
open class PlayerClient: PlayerModel {

    override val tehai:
            Tehai = Tehai()
    override val kawa: Kawa = Kawa()
    override val mentsu: MutableList<Mentsu> = mutableListOf()
    override var tsumo: TsumoHaiModel = TsumoHaiModel()

    override var point: Int = 0
}