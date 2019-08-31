package ru.skillbranch.devintensive.models.data

import androidx.annotation.VisibleForTesting
import ru.skillbranch.devintensive.extensions.shortFormat
import ru.skillbranch.devintensive.models.BaseMessage
import ru.skillbranch.devintensive.models.ImageMessage
import ru.skillbranch.devintensive.models.TextMessage
import ru.skillbranch.devintensive.utils.Utils
import java.util.*

data class Chat(
    val id: String,
    val title: String,
    val members: List<User> = listOf(),
    var messages: MutableList<BaseMessage> = mutableListOf(),
    var isArchived: Boolean = false
) {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun unreadableMessageCount(): Int = messages.filter { !it.isReaded }.size

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun lastMessageDate(): Date? = messages.lastOrNull()?.date

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun lastMessageShort(): Pair<String, String?> = when(val lastMessage = messages.lastOrNull()){
        is TextMessage -> (lastMessage.text ?: "") to lastMessage.from.firstName
        is ImageMessage -> "${lastMessage.from.firstName} - отправил фото" to lastMessage.from.firstName  //TODO в ресурсы
        else -> "" to null
    }

    private fun isSingle(): Boolean = members.size == 1

    fun toChatItem(): ChatItem {
        return if (isSingle()) {
            val user = members.first()
            ChatItem(
                id,
                user.avatar,
                Utils.toInitials(user.firstName, user.lastName) ?: "??",
                getTitle(user.firstName to user.lastName),
                lastMessageShort().first,
                unreadableMessageCount(),
                lastMessageDate()?.shortFormat(),
                user.isOnline
            )
        } else {
            ChatItem(
                id,
                null,
                "",
                title,
                lastMessageShort().first,
                unreadableMessageCount(),
                lastMessageDate()?.shortFormat(),
                false,
                ChatType.GROUP,
                lastMessageShort().second
            )
        }
    }

    private fun getTitle(names: Pair<String?, String?>): String = when {
        names.first == null && names.second == null -> ""
        names.first == null && names.second != null-> names.second!!
        names.first != null && names.second == null-> names.first!!
        else -> "${names.first} ${names.second}"
    }

    companion object {
        fun archivedToChatItem(chats: List<Chat>): ChatItem{
            val lastMsgChat = chats.maxWith (Comparator { chat0, chat1 ->
                when{
                    chat0?.lastMessageDate()?.time ?: 0 > chat1?.lastMessageDate()?.time ?: 0 -> 1
                    chat0?.lastMessageDate()?.time ?: 0 < chat1?.lastMessageDate()?.time ?: 0 -> -1
                    else -> 0
                }})
            return ChatItem(
                "-1",
                null,
                "",
                "",
                lastMsgChat?.lastMessageShort()?.first,
                chats.sumBy { it.unreadableMessageCount() },
                lastMsgChat?.lastMessageDate()?.shortFormat(),
                false,
                ChatType.ARCHIVE,
                lastMsgChat?.lastMessageShort()?.second
            )
        }
    }
}

enum class ChatType{
    SINGLE,
    GROUP,
    ARCHIVE
}



