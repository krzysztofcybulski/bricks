import React from 'react';
import VerticalBox from '../../atoms/VerticalBox';
import HorizontalBox from '../../atoms/HorizontalBox';
import Avatar from '../../atoms/Avatar';
import Text from '../../atoms/Text';
import { gray, white } from '../../../utils/colors';
import { Trophy } from 'grommet-icons';

const GameChooser = ({ games, selected, onSelect }) =>
    <HorizontalBox gap="small"
                   pad="small"
                   flex={{ shrink: 0 }}
                   overflow={{ horizontal: 'scroll' }}>
        {games.map(({ id, players, winner }) =>
            <VerticalBox
                key={id}
                onClick={() => onSelect(id)}
                pad="small"
                flex={{ shrink: 0 }}
                style={{ backgroundColor: selected === id ? gray : white, borderRadius: 8 }}>
                {players.map(({ id, name, avatarUrl }) =>
                    <HorizontalBox gap="small" align="center" key={name}>
                        <Avatar size="small" url={avatarUrl}/>
                        <Text>{name}</Text>
                        {winner === id && <Trophy size="small"/>}
                    </HorizontalBox>
                )}
            </VerticalBox>
        )}
    </HorizontalBox>;

export default GameChooser;
