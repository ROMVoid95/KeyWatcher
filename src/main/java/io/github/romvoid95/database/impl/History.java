package io.github.romvoid95.database.impl;

import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class History
{
	private HashMap<String, List<Long>> gameMap = new HashMap<>();
}
