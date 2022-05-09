import React, { useState } from 'react';
import VerticalBox from '../components/atoms/VerticalBox';
import { connect } from 'react-redux';
import AvatarHeader from '../components/molecules/AvatarHeader';
import PlayersList from '../components/structures/PlayersList';
import Button from '../components/atoms/Button';
import LoadingContainer from '../components/molecules/LoadingContainer';
import TournamentSettings from '../components/structures/TournamentSettings';
import { addBot, startTournament } from '../redux/slices/lobbiesReducer';
import { white } from '../utils/colors';

const LobbyDetails = ({ lobby, bots, addBot, loading, startTournament }) => {
    const [settings, setSettings] = useState();
    return lobby && <VerticalBox pad="medium"
                                 elevation="medium" height="100%" style={{ backgroundColor: white }}>
        <LoadingContainer loadIf={loading}>
            <>
                <AvatarHeader image={lobby.image} text={lobby.name}/>
                <VerticalBox gap="medium" overflow={{ vertical: 'scroll', horizontal: 'hidden' }}>
                    {lobby.status === 'OPEN'
                        ? <>
                            <PlayersList players={lobby.players.map(p => ({ ...p, text: `${p.ping || 0} ms` }))}
                                         bots={bots}
                                         addBot={(botId) => addBot({ botId, lobbyId: lobby.id })}/>
                            <TournamentSettings setSettings={setSettings}/>
                        </>
                        : <PlayersList players={lobby.players.map(p => ({ ...p, text: `${p.points} points` }))}/>
                    }
                </VerticalBox>
                <VerticalBox flex="grow" justify="end" margin={{ top: 'large' }}>
                    {lobby.status === 'OPEN' &&
                        <Button onClick={() => startTournament({
                            lobbyId: lobby.id,
                            settings
                        })}>Start tournament</Button>}
                </VerticalBox>
            </>
        </LoadingContainer>
    </VerticalBox>;
};

export default connect(
    ({ lobbies, players }) => ({
        loading: lobbies.loadingLobby,
        lobby: lobbies.selected && {
            ...lobbies.selected,
            players: lobbies.selected.players.map(p => ({ ...p, ...players[p.name] }))
        },
        bots: lobbies?.allBots
    }),
    (dispatch) => ({
        addBot: ({ botId, lobbyId }) => dispatch(addBot({ botId, lobbyId })),
        startTournament: ({ lobbyId, settings }) => dispatch(startTournament({ lobbyId, settings }))
    })
)(LobbyDetails);
