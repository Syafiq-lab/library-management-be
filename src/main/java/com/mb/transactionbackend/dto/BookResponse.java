package com.mb.transactionbackend.dto;

public record BookResponse(
		String bookId,
		String isbn,
		String title,
		String author,
		boolean borrowed
) {}
