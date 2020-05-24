package com.mawistudios.data.local

import io.objectbox.kotlin.boxFor

object SessionRepo : BaseRepo<Session>(ObjectBox.boxStore.boxFor()) {

}