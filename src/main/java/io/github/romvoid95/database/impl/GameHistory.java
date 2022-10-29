package io.github.romvoid95.database.impl;

import java.util.List;

import javax.annotation.Nullable;

public record GameHistory(String name, @Nullable List<Long> dates)
{
	
}
