package com.mawistudios.data.local

import io.objectbox.kotlin.boxFor

interface ISessionRepo : IBaseRepo<Session>
class SessionRepo : BaseRepo<Session>(ObjectBox.boxStore.boxFor()), ISessionRepo {

}