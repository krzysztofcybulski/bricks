import HorizontalBox from '../../atoms/HorizontalBox';
import Slider from '../../atoms/Slider';
import { CaretNext, CaretPrevious, PauseFill, PlayFill, Stop } from 'grommet-icons';
import { useEffect, useState } from 'react';
import VerticalBox from '../../atoms/VerticalBox';

const GameControls = ({ time, max, setTime }) => {

    const [playing, setPlaying] = useState(false);
    useEffect(() => {
        const interval = setInterval(() => {
            if (playing) {
                setTime(t => t + 1);
            }
        }, 100);
        return () => {
            clearInterval(interval);
        };
    }, [playing, setTime]);

    useEffect(() => {
        if (time === 0 || time === max) {
            setPlaying(false);
        }
    }, [time, max]);

    return <VerticalBox pad="small" gap="small">
        <Slider min={0} max={max} value={time} onChange={value => {
            setPlaying(false);
            setTime(parseInt(value));
        }}/>
        <HorizontalBox justify="center">
            <CaretPrevious cursor="pointer" onClick={() => setTime(time - 1)}/>
            <Stop cursor="pointer" onClick={() => {
                setPlaying(false);
                setTime(0);
            }}/>
            {!playing && <PlayFill cursor="pointer" onClick={() => {
                setPlaying(true);
            }}/>}
            {playing && <PauseFill cursor="pointer" onClick={() => {
                setPlaying(false);
            }}/>}
            <CaretNext cursor="pointer" onClick={() => setTime(time + 1)}/>
        </HorizontalBox>
    </VerticalBox>;
};

export default GameControls;

