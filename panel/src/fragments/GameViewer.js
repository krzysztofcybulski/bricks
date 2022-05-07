import React from 'react';
import { connect } from 'react-redux';
import GameChooser from '../components/structures/game/GameChooser';
import VerticalBox from '../components/atoms/VerticalBox';
import { fetchGame } from '../redux/slices/gameReducer';
import GamePanel from '../components/structures/game/GamePanel';

import { ReactComponent as NoGame } from '../images/no_game.svg';

const GameViewer = ({ gameView, selectedGame, selectGame, games }) => {

    return <VerticalBox flex height="100%">
        <GameChooser games={games} onSelect={selectGame} selected={selectedGame}/>
        {
            gameView ?
                <VerticalBox align="center" justify="center"
                             pad={{ top: 'large' }}
                             overflow={{ vertical: 'scroll' }}>
                    <VerticalBox margin="large"
                                 align="stretch"
                                 elevation="medium"
                                 flex={{ shrink: 0 }}>
                        <GamePanel gameView={gameView}/>
                    </VerticalBox>
                </VerticalBox>
                : <VerticalBox align="center" justify="center">
                    <NoGame style={{ width: 256}} />
                </VerticalBox>
        }
    </VerticalBox>;
};

export default connect(
    ({ lobbies: { selected }, games: { gameView } }) => ({
        games: selected?.games || [],
        selectedGame: gameView?.id,
        gameView
    }),
    (dispatch) => ({
        selectGame: (id) => dispatch(fetchGame({ id }))
    })
)(GameViewer);
