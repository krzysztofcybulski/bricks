import React from 'react';
import { TextInput } from 'grommet';
import { Search as SearchIcon } from 'grommet-icons';

const Search = ({ value, onChange, suggestions = [] }) =>
    <TextInput icon={<SearchIcon/>}
               value={value}
               onChange={({ target }) => onChange(target.value)}
               suggestions={suggestions.filter(s => s.indexOf(value) >= 0)}
               onSuggestionSelect={({ suggestion }) => onChange(suggestion)}
               focusIndicator={false}
    />;

export default Search;
