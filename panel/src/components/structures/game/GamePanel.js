import { useEffect, useState } from 'react';

import VerticalBox from '../../atoms/VerticalBox';
import GameCanvas from './GameCanvas';
import GameControls from './GameControls';
import GameHeader from './GameHeader';
import HorizontalBox from '../../atoms/HorizontalBox';

const GamePanel = ({ gameView }) => {
    const [time, setTime] = useState(0);

    useEffect(() => {
        setTime(0);
    }, [gameView.id]);

    return <VerticalBox pad="medium"
                        gap="small"
                        style={{ minWidth: 320 }}>
        <GameHeader players={gameView.players}/>
        <HorizontalBox justify="center">
            <GameCanvas blocks={gameView.blocks.filter(({ move }) => move <= time)}
                        mapSize={gameView.mapSize}
                        canvasSize={800}/>
        </HorizontalBox>
        <GameControls time={time} setTime={setTime} max={gameView.maxTime}/>
    </VerticalBox>;
};

export default GamePanel;

