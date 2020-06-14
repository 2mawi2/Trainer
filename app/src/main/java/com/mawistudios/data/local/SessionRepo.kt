package com.mawistudios.data.local

import io.objectbox.kotlin.boxFor
import io.objectbox.kotlin.query

interface ISessionRepo : IBaseRepo<Session> {
    fun getLastActiveOrNull(): Session?
}

class SessionRepo : BaseRepo<Session>(ObjectBox.boxStore.boxFor()), ISessionRepo {
    override fun getLastActiveOrNull(): Session? {
        return box.query { isNull(Session_.startTime) }.findFirst()
    }
}