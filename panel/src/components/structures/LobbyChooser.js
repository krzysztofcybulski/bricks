import React from 'react';
import VerticalBox from '../atoms/VerticalBox';
import LobbyButton from '../molecules/LobbyButton';
import Section from '../molecules/Section';

const LobbyChooser = ({ lobbies, selectedLobby, onClick }) =>
    <VerticalBox gap="small" overflow={{ vertical: "scroll" }}>
        <LobbiesSection
            label="Open"
            lobbies={lobbies.filter(({ status }) => status === 'OPEN')}
            selectedLobby={selectedLobby}
            onClick={onClick}
        />
        <LobbiesSection
            label="In game"
            lobbies={lobbies.filter(({ status }) => status === 'IN_GAME')}
            selectedLobby={selectedLobby}
            onClick={onClick}
        />
        <LobbiesSection
            label="Closed"
            lobbies={lobbies.filter(({ status }) => status === 'CLOSED')}
            selectedLobby={selectedLobby}
            onClick={onClick}
        />
    </VerticalBox>;


const LobbiesSection = ({ label, lobbies, selectedLobby, onClick }) => {
    if(lobbies.length === 0) {
        return <></>;
    }

    return <Section label={label}>
        <VerticalBox>
            {lobbies.map(lobby =>
                <LobbyButton
                    key={lobby.name}
                    lobby={lobby}
                    onClick={() => onClick(lobby.id)}
                    selected={selectedLobby === lobby.id}
                />
            )}
        </VerticalBox>
    </Section>;
};

export default LobbyChooser;
