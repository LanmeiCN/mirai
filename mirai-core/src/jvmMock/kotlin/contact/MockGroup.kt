/*
 * Copyright 2019-2021 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/dev/LICENSE
 */

@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package net.mamoe.mirai.mock.contact

import net.mamoe.kjbb.JvmBlockingBridge
import net.mamoe.mirai.contact.ContactList
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.NormalMember
import net.mamoe.mirai.data.MemberInfo
import net.mamoe.mirai.event.broadcast
import net.mamoe.mirai.event.events.MemberJoinRequestEvent
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.mock.MockBot
import net.mamoe.mirai.mock.MockBotDSL
import net.mamoe.mirai.mock.contact.announcement.MockAnnouncements
import net.mamoe.mirai.mock.userprofile.MockMemberInfoBuilder
import net.mamoe.mirai.utils.JavaFriendlyAPI
import net.mamoe.mirai.utils.cast
import java.util.function.Consumer
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.internal.LowPriorityInOverloadResolution
import kotlin.random.Random

@JvmBlockingBridge
public interface MockGroup : Group, MockContact {
    public var uin: Long
    override val bot: MockBot
    override val members: ContactList<MockNormalMember>
    override val owner: MockNormalMember
    override val botAsMember: MockNormalMember
    override val announcements: MockAnnouncements

    @MockBotDSL
    public fun addMember(mockMember: MemberInfo): MockGroup //  chain call

    @MockBotDSL
    public fun addMember0(mockMember: MemberInfo): MockNormalMember

    @MockBotDSL
    @JavaFriendlyAPI
    @LowPriorityInOverloadResolution
    public fun addMember(id: Long, nick: String, action: Consumer<MockMemberInfoBuilder>): MockGroup {
        return addMember(MockMemberInfoBuilder().uin(id).nick(nick).also { action.accept(it) }.build())
    }

    // Will have event broadcast
    @MockBotDSL
    public suspend fun changeOwner(member: NormalMember)

    @MockBotDSL
    public fun changeOwnerNoEventBroadcast(member: NormalMember)

    @MockBotDSL
    public fun newAnonymous(nick: String, id: String): MockAnonymousMember

    override fun get(id: Long): MockNormalMember?
    override fun getOrFail(id: Long): MockNormalMember = super.getOrFail(id).cast()

    override suspend fun says(message: MessageChain): Nothing {
        throw UnsupportedOperationException("Group cannot saying a message")
    }

    override suspend fun says(message: String): Nothing {
        throw UnsupportedOperationException("Group cannot saying a message")
    }

    override suspend fun says(message: Message): Nothing {
        throw UnsupportedOperationException("Group cannot saying a message")
    }

    @MockBotDSL
    public suspend fun broadcastNewMemberJoinEvent(
        requester: Long,
        requesterName: String,
        message: String,
        invitor: Long = 0L,
    ): MemberJoinRequestEvent {
        return MemberJoinRequestEvent(
            bot, Random.nextLong(),
            message,
            requester,
            this.id,
            this.name,
            requesterName,
            invitor.takeIf { it != 0L },
        ).broadcast()
    }
}

@MockBotDSL
public inline fun MockGroup.addMember(id: Long, nick: String, action: MockMemberInfoBuilder.() -> Unit): MockGroup {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
    return addMember(MockMemberInfoBuilder().uin(id).nick(nick).also(action).build())
}
