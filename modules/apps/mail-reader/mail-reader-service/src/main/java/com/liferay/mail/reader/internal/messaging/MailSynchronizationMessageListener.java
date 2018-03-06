/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.mail.reader.internal.messaging;

import com.liferay.mail.reader.exception.NoSuchAccountException;
import com.liferay.mail.reader.internal.mailbox.MailboxFactoryUtil;
import com.liferay.mail.reader.mailbox.Mailbox;
import com.liferay.mail.reader.service.AccountLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Scott Lee
 * @author Ryan Park
 */
public class MailSynchronizationMessageListener extends BaseMessageListener {

	@Override
	protected void doReceive(Message message) throws Exception {
		String command = message.getString("command");

		if (command.equals("synchronize")) {
			synchronize(message);
		}
		else if (command.equals("flag")) {
			flagMessage(message);
		}
	}

	protected void flagMessage(Message message) throws Exception {
		long userId = message.getLong("userId");
		long accountId = message.getLong("accountId");
		String password = message.getString("password");
		long folderId = message.getLong("folderId");
		long messageId = message.getLong("messageId");
		int flag = message.getInteger("flag");
		boolean flagValue = message.getBoolean("flagValue");

		if (_log.isDebugEnabled()) {
			_log.debug("Flagging message for messageId " + messageId);
		}

		if (Validator.isNull(password)) {
			return;
		}

		Mailbox mailbox = MailboxFactoryUtil.getMailbox(
			UserLocalServiceUtil.getUser(userId),
			AccountLocalServiceUtil.getAccount(accountId), password);

		mailbox.updateFlags(folderId, new long[] {messageId}, flag, flagValue);
	}

	protected void synchronize(Message message) throws Exception {
		long userId = message.getLong("userId");
		long accountId = message.getLong("accountId");
		String password = message.getString("password");
		long folderId = message.getLong("folderId");
		long messageId = message.getLong("messageId");
		int pageNumber = message.getInteger("pageNumber");
		int messagesPerPage = message.getInteger("messagesPerPage");

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Starting synch for accountId ", String.valueOf(accountId),
					" folderId ", String.valueOf(folderId), " and messageId ",
					String.valueOf(messageId)));
		}

		try {
			if (!password.equals(StringPool.BLANK)) {
				Mailbox mailbox = MailboxFactoryUtil.getMailbox(
					UserLocalServiceUtil.getUser(userId),
					AccountLocalServiceUtil.getAccount(accountId), password);

				if (messageId != 0) {
					mailbox.synchronizeMessage(messageId);
				}
				else if (folderId != 0) {
					if (pageNumber != 0) {
						mailbox.synchronizePage(
							folderId, pageNumber, messagesPerPage);
					}
					else {
						mailbox.synchronizeFolder(folderId);
					}
				}
				else {
					mailbox.synchronize();
				}
			}
			else {
				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Unable to acquire synch lock for accountId ",
							String.valueOf(accountId), " and folderId ",
							String.valueOf(folderId), " and messageId ",
							String.valueOf(messageId)));
				}
			}
		}
		catch (NoSuchAccountException nsae) {
			if (_log.isDebugEnabled()) {
				_log.debug("Skipping syncronization of accountId " + accountId);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MailSynchronizationMessageListener.class);

}