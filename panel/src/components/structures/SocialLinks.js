import React from 'react';
import { Accordion, AccordionPanel, Anchor, Text } from 'grommet';
import PlayerInList from '../molecules/PlayerInList';
import ChooserWithAdd from '../molecules/ChooserWithAdd';
import HorizontalBox from '../atoms/HorizontalBox';

const SocialLinks = () =>
    <HorizontalBox gap="small" justify="center">
        <Anchor href="https://github.com/krzysztofcybulski/bricks" label="Github"/>
        <Text>|</Text>
        <Anchor href="mailto:krzysztofpcy@gmail.com" label="Contact"/>
    </HorizontalBox>

export default SocialLinks;
