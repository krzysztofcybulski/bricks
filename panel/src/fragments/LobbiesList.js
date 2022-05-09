import React, { useEffect, useState } from 'react';
import VerticalBox from '../components/atoms/VerticalBox';
import Search from '../components/molecules/Search';
import LobbyChooser from '../components/structures/LobbyChooser';
import { connect } from 'react-redux';
import { createLobby, selectLobby } from '../redux/slices/lobbiesReducer';
import ProfileHeader from '../components/structures/ProfileHeader';
import Button from '../components/atoms/Button';
import { white } from '../utils/colors';
import ApiKeyGenerator from '../components/structures/ApiKeyGenerator';
import SocialLinks from '../components/structures/SocialLinks';
import { hasPermission } from '../redux/auth';

const LobbiesList = ({ createLobby, lobbies, selectedLobby, selectLobby }) => {
    const [search, setSearch] = useState('');

    useEffect(() => {
        if (selectedLobby) {
            selectLobby({ id: selectedLobby.id });
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [selectLobby, lobbies]);

    return <VerticalBox pad="medium" justify="between" elevation="medium" height="100%"
                        style={{ backgroundColor: white }}>
        <VerticalBox gap="medium">
            <ProfileHeader/>
            <Search value={search}
                    onChange={setSearch}
                    suggestions={lobbies.map(l => l.name)}/>
            <LobbyChooser lobbies={lobbies.filter(l => l.name.indexOf(search) >= 0)}
                          selectedLobby={selectedLobby?.id}
                          onClick={id => selectLobby({ id })}/>
        </VerticalBox>
        <VerticalBox gap="small">
            <ApiKeyGenerator />
            { hasPermission("add:lobbies") && <Button onClick={createLobby}>Create lobby</Button> }
            <SocialLinks />
        </VerticalBox>
    </VerticalBox>;
};

export default connect(
    ({ lobbies: { all, selected } }) => ({
        lobbies: all,
        selectedLobby: selected
    }),
    (dispatch) => ({
        selectLobby: ({ id }) => dispatch(selectLobby({ id })),
        createLobby: () => dispatch(createLobby())
    })
)(LobbiesList);
