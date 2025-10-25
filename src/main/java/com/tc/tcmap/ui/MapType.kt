package com.tc.tcmap.ui

import androidx.compose.runtime.Composable
import com.tc.tcmap.domain.MarkerInfo
import com.tc.tcmap.domain.PersonInfo

sealed class MapType<out T> {
    object SimpleMap : MapType<Nothing>()
    data class MarkedMapWithPeople(val people : List<PersonInfo>,
                                   val onPersonClick : (PersonInfo) -> Unit
                                   ) : MapType<PersonInfo>()
    data class MarkedMap<T>(val data : List<T>, val itemContent : @Composable (T) -> Unit
        ) : MapType<T>()
}