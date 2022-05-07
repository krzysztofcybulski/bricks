import React from 'react';
import HorizontalBox from '../atoms/HorizontalBox';
import Avatar from '../atoms/Avatar';
import Header from '../atoms/Header';
import Text from '../atoms/Text';

const PlayerInList = ({ player: { name, avatarUrl, text } }) =>
    <HorizontalBox gap="medium" pad="small" justify="between">
        <HorizontalBox gap="medium" align="center">
            <Avatar url={avatarUrl} size="medium"/>
            <Header>{name}</Header>
        </HorizontalBox>
        <Text>{text}</Text>
    </HorizontalBox>;

export default PlayerInList;
