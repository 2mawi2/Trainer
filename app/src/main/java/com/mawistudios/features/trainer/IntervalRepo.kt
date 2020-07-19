package com.mawistudios.features.trainer

import com.mawistudios.app.model.Interval
import com.mawistudios.data.local.BaseRepo
import com.mawistudios.data.local.IBaseRepo
import com.mawistudios.data.local.ObjectBox
import io.objectbox.kotlin.boxFor


interface IIntervalRepo : IBaseRepo<Interval>
class IntervalRepo : BaseRepo<Interval>(
    ObjectBox.boxStore.boxFor()),
    IIntervalRepo


