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

package com.liferay.apio.architect.sample.internal.model;

import com.github.javafaker.Book;
import com.github.javafaker.DateAndTime;
import com.github.javafaker.Faker;
import com.github.javafaker.Lorem;
import com.github.javafaker.service.RandomService;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a blog posting. This is a mock class for sample purposes only. It
 * contains methods for creating, retrieving, updating, and deleting blog posts
 * in an in-memory database with fake data.
 *
 * @author Alejandro Hernández
 */
public class BlogPostingModel {

	/**
	 * Adds a new blog posting to the database.
	 *
	 * @param  content the blog posting's content
	 * @param  creatorId the ID of the blog posting's creator
	 * @param  subtitle the blog posting's subtitle
	 * @param  title the blog posting's title
	 * @return the new blog posting
	 */
	public static BlogPostingModel addBlogPosting(
		String content, long creatorId, String subtitle, String title) {

		long blogPostingId = _count.incrementAndGet();

		BlogPostingModel blogPostingModel = new BlogPostingModel(
			blogPostingId, content, new Date(), creatorId, new Date(), subtitle,
			title);

		_blogPostings.put(blogPostingId, blogPostingModel);

		return blogPostingModel;
	}

	/**
	 * Compute the fake data for this model class.
	 *
	 * @review
	 */
	public static void computeBlogPostingModels() {
		for (long blogPostingId = 0; blogPostingId < 42; blogPostingId++) {
			Faker faker = new Faker();

			Book book = faker.book();

			Lorem lorem = faker.lorem();

			List<String> paragraphs = lorem.paragraphs(5);

			Stream<String> stream = paragraphs.stream();

			String content = stream.map(
				paragraph -> "<p>" + paragraph + "</p>"
			).collect(
				Collectors.joining()
			);

			RandomService randomService = faker.random();

			int creatorId = randomService.nextInt(PersonModel.getPeopleCount());

			DateAndTime dateAndTime = faker.date();

			Date date = dateAndTime.past(400, TimeUnit.DAYS);

			BlogPostingModel blogPostingModel = new BlogPostingModel(
				blogPostingId, content, date, creatorId, date, lorem.sentence(),
				book.title());

			_blogPostings.put(blogPostingId, blogPostingModel);
		}
	}

	/**
	 * Deletes the blog posting that matches the specified ID.
	 *
	 * @param blogPostingId the blog posting's ID
	 */
	public static void deleteBlogPosting(long blogPostingId) {
		_blogPostings.remove(blogPostingId);
	}

	/**
	 * Returns the blog posting that matches the specified ID, if that blog
	 * posting exists. Returns {@code Optional#empty()} otherwise.
	 *
	 * @param  blogPostingId the blog posting's ID
	 * @return the blog posting, if present; {@code Optional#empty()} otherwise
	 */
	public static Optional<BlogPostingModel> getBlogPosting(
		long blogPostingId) {

		BlogPostingModel blogPostingModel = _blogPostings.get(blogPostingId);

		return Optional.ofNullable(blogPostingModel);
	}

	/**
	 * Returns the total number of blog postings.
	 *
	 * @return the total number of blog postings
	 */
	public static int getBlogPostingCount() {
		return _blogPostings.size();
	}

	/**
	 * Returns the page of blog postings specified by the page's start and end
	 * positions.
	 *
	 * @param  start the page's start position
	 * @param  end the page's end position
	 * @return the page of blog postings
	 */
	public static List<BlogPostingModel> getBlogPostings(int start, int end) {
		Collection<BlogPostingModel> blogPostingModels = _blogPostings.values();

		Stream<BlogPostingModel> stream = blogPostingModels.stream();

		return stream.skip(
			start
		).limit(
			end
		).collect(
			Collectors.toList()
		);
	}

	/**
	 * Updates the blog posting that matches the specified ID, if that blog
	 * posting exists. Returns {@code Optional#empty()} otherwise.
	 *
	 * @param  blogPostingId the blog posting's ID
	 * @param  content the blog posting's new content
	 * @param  creatorId the ID of the user updating the blog posting
	 * @param  subtitle the blog posting's new subtitle
	 * @param  title the blog posting's new title
	 * @return the updated blog posting, if present; {@code Optional#empty()}
	 *         otherwise
	 */
	public static Optional<BlogPostingModel> updateBlogPosting(
		long blogPostingId, String content, long creatorId, String subtitle,
		String title) {

		BlogPostingModel blogPostingModel = _blogPostings.get(blogPostingId);

		if (blogPostingModel == null) {
			return Optional.empty();
		}

		Date createDate = blogPostingModel.getCreateDate();

		blogPostingModel = new BlogPostingModel(
			blogPostingId, content, createDate, creatorId, new Date(), subtitle,
			title);

		_blogPostings.put(blogPostingId, blogPostingModel);

		return Optional.of(blogPostingModel);
	}

	/**
	 * Returns the current blog posting's ID.
	 *
	 * @return the current blog posting's ID
	 */
	public long getBlogPostingId() {
		return _blogPostingId;
	}

	/**
	 * Returns the current blog posting's content.
	 *
	 * @return the current blog posting's content
	 */
	public String getContent() {
		return _content;
	}

	/**
	 * Returns the date that the current blog posting was created.
	 *
	 * @return the current blog posting's creation date
	 */
	public Date getCreateDate() {
		return new Date(_createDate.getTime());
	}

	/**
	 * Returns the ID of the current blog posting's creator.
	 *
	 * @return the ID of the current blog posting's creator
	 */
	public long getCreatorId() {
		return _creatorId;
	}

	/**
	 * Returns the date that the current blog posting was modified.
	 *
	 * @return the current blog posting's modification date
	 */
	public Date getModifiedDate() {
		return new Date(_modifiedDate.getTime());
	}

	/**
	 * Returns the current blog posting's subtitle.
	 *
	 * @return the current blog posting's subtitle
	 */
	public String getSubtitle() {
		return _subtitle;
	}

	/**
	 * Returns the current blog posting's title.
	 *
	 * @return the current blog posting's title
	 */
	public String getTitle() {
		return _title;
	}

	private BlogPostingModel(
		long blogPostingId, String content, Date createDate, long creatorId,
		Date modifiedDate, String subtitle, String title) {

		_blogPostingId = blogPostingId;
		_content = content;
		_createDate = createDate;
		_creatorId = creatorId;
		_modifiedDate = modifiedDate;
		_subtitle = subtitle;
		_title = title;
	}

	private static final Map<Long, BlogPostingModel> _blogPostings =
		new ConcurrentHashMap<>();
	private static final AtomicLong _count = new AtomicLong(29);

	private final long _blogPostingId;
	private final String _content;
	private final Date _createDate;
	private final long _creatorId;
	private final Date _modifiedDate;
	private final String _subtitle;
	private final String _title;

}