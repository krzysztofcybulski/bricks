import React from 'react';
import VerticalBox from '../atoms/VerticalBox';
import Avatar from '../atoms/Avatar';
import Header from '../atoms/Header';

const AvatarHeader = ({ image, text }) =>
    <VerticalBox gap="medium" pad="medium" align="center" flex={{ shrink: 0}}>
        <Avatar url={image} size="large" />
        <Header>{text}</Header>
    </VerticalBox>;

export default AvatarHeader;
