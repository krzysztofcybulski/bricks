import React from 'react';
import HorizontalBox from '../atoms/HorizontalBox';
import Avatar from '../atoms/Avatar';
import VerticalBox from '../atoms/VerticalBox';
import Header from '../atoms/Header';
import Properties from './Properties';
import { gray, white } from '../../utils/colors';

const LobbyButton = ({ lobby: { name, image, winner, gamesCount, playersCount, createdAt }, onClick, selected }) =>
    <HorizontalBox gap="small"
                   pad="small"
                   align="center"
                   onClick={onClick}
                   style={{
                       backgroundColor: selected ? gray : white,
                       borderRadius: 8,
                       flexShrink: 0
                   }}>
        <Avatar url={image} size="small"/>
        <VerticalBox>
            <Header>{name}</Header>
            <Properties
                properties={[
                    winner,
                    gamesCount && `${gamesCount} games`,
                    `${playersCount} players`,
                    timeDiffToNow(createdAt)
                ]}
            />
        </VerticalBox>
    </HorizontalBox>;

const timeDiffToNow = (time) => {
    const mins = Math.floor((new Date() - new Date(time)) / 6e4);
    if (mins === 0) return 'Just now';
    if (mins > 30) return 'Half hour';
    if (mins > 60) return 'Hour';
    if (mins > 120) return `${mins / 60} hours`;
    return mins + ' min';
};

export default LobbyButton;
