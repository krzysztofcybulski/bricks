import React from 'react';
import { Layer, Rect, Stage } from 'react-konva';

const GameCanvas = ({ blocks, mapSize, onHover = () => {}, currentBlock, canvasSize = 500 }) =>{
    const tileSize = Math.min(canvasSize / mapSize, 38);
    return <Stage width={mapSize * tileSize} height={mapSize * tileSize} backgroundColor="black">
        <Layer>
            <Rect x={0} y={0} height={mapSize * tileSize} width={mapSize * tileSize} fill="#f2f2f2"/>
            {blocks.map(({ x, y, move, player, color }, i) =>
                <Rect key={`${x}x${y}_${i}_${player}`}
                      onMouseMove={e => {
                          const container = e.target.getStage().container();
                          container.style.cursor = "pointer";
                          onHover({ x, y, move, player });
                      }}
                      onMouseOut={e => {
                          const container = e.target.getStage().container();
                          container.style.cursor = "default";
                          onHover(null);
                      }}
                      x={x * tileSize}
                      y={y * tileSize}
                      height={tileSize}
                      width={tileSize}
                      stroke={(blocks.length > 0 && blocks[blocks.length - 1].move === move) ? 'black' : '#c4c4c4'}
                      strokeWidth={1}
                      fill={color}/>)}
        </Layer>
    </Stage>;
};

export default GameCanvas;
