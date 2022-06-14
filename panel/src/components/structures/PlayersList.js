import React from 'react';
import { Accordion, AccordionPanel } from 'grommet';
import PlayerInList from '../molecules/PlayerInList';
import ChooserWithAdd from '../molecules/ChooserWithAdd';
import { hasPermission } from "../../redux/auth";

const PlayersList = ({ players, bots, addBot }) =>
    <Accordion flex={{ shrink: 0 }}>
        <AccordionPanel label="Players">
            {players.map(player => <PlayerInList player={player} key={player.name}/>)}
            {bots && hasPermission('add:bots') &&
                <ChooserWithAdd
                    options={bots.map(b => ({ name: b.name, value: b.name }))}
                    add={addBot}/>
            }
        </AccordionPanel>
    </Accordion>;

export default PlayersList;
