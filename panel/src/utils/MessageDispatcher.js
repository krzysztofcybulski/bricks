import { connect } from 'react-redux';
import NotificationContainer from 'react-notifications/lib/NotificationContainer';
import { useEffect } from 'react';
import { wsAddress } from '../redux/api';
import { NotificationManager } from 'react-notifications';
import { fetchLobbies } from '../redux/slices/lobbiesReducer';

const MessageDispatcher = ({ onMessage, children }) => {
    useEffect(() => {
        const ws = new WebSocket(`${wsAddress}/lobbies/updates`);
        ws.onmessage = event => {
            onMessage(JSON.parse(event.data));
        };
        return () => {
            ws.close();
        }
    }, [onMessage]);

    return <>
        <NotificationContainer/>
        {children}
    </>;
};


export default connect(
    null,
    dispatch => ({
        onMessage: (message) => {
            switch (message.type) {
                case "GAME_ENDED":
                    dispatch(fetchLobbies());
                    break;
                case "PLAYER_JOINED":
                    dispatch(fetchLobbies());
                    NotificationManager.info(`${message.player} joined`);
                    break;
                case "PLAYER_LEFT":
                    dispatch(fetchLobbies());
                    NotificationManager.info(`${message.player} left`);
                    break;
                case "LOBBY_ADDED":
                    dispatch(fetchLobbies());
                    NotificationManager.info(`${message.lobby} lobby added`);
                    break;
                case "TOURNAMENT_STARTED":
                    dispatch(fetchLobbies());
                    NotificationManager.info(`New tournament started`);
                    break;
                case "TOURNAMENT_ENDED":
                    dispatch(fetchLobbies());
                    NotificationManager.info(`Tournament ended`);
                    break;
                case "REPORT_PING":
                    break;
                default: break;
            }
        }
    }))
(MessageDispatcher);
