package com.mawistudios.data.local

import com.mawistudios.app.model.Interval
import com.mawistudios.app.model.Workout
import com.mawistudios.app.model.Workout_
import io.objectbox.kotlin.boxFor
import java.lang.Exception
import java.lang.IllegalStateException


interface IIntervalRepo : IBaseRepo<Interval>
class IntervalRepo : BaseRepo<Interval>(ObjectBox.boxStore.boxFor()), IIntervalRepo