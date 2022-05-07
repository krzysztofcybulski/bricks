import React, { useEffect, useState } from 'react';
import { Accordion, AccordionPanel, CheckBox, RangeInput, Select } from 'grommet';
import Text from '../atoms/Text';
import HorizontalBox from '../atoms/HorizontalBox';
import VerticalBox from '../atoms/VerticalBox';

const availableSizes = [5, 9, 12, 20, 32, 64];

const TournamentSettings = ({ setSettings = () => {} }) => {

    const [sizes, setSizes] = useState([5, 9, 12]);
    const [initTime, setInitTime] = useState(1000);
    const [moveTime, setMoveTime] = useState(300);

    useEffect(() => {
        setSettings({ sizes, initTime, moveTime });
    }, [ sizes, initTime, moveTime, setSettings ]);

    return <Accordion>
        <AccordionPanel label="Settings">
            <Select options={['Deathmatch']} value="Deathmatch"/>
            <Text>Map sizes</Text>
            <HorizontalBox wrap>
                {availableSizes.map(size =>
                    <VerticalBox width="50%" margin={{ bottom: 'small' }} key={size}>
                        <CheckBox
                            checked={sizes.indexOf(size) >= 0}
                            label={`${size} x ${size}`}
                            onChange={(event) => {
                                if (event.target.checked) {
                                    setSizes(sizes => [...sizes, size]);
                                } else {
                                    setSizes(sizes => sizes.filter(s => s !== size));
                                }
                            }}
                        />
                    </VerticalBox>)}
            </HorizontalBox>
            <HorizontalBox justify="between">
                <Text>Init time</Text>
                <Text>{initTime}ms</Text>
            </HorizontalBox>
            <RangeInput
                value={initTime} min={100} max={5000} step={100}
                onChange={({ target }) => setInitTime(target.value)}
            />
            <HorizontalBox justify="between">
                <Text>Move time</Text>
                <Text>{moveTime}ms</Text>
            </HorizontalBox>
            <RangeInput
                value={moveTime} min={100} max={5000} step={100}
                onChange={({ target }) => setMoveTime(target.value)}
            />
        </AccordionPanel>
    </Accordion>;
};

export default TournamentSettings;
