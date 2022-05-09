import { configureStore } from '@reduxjs/toolkit';
import lobbiesReducer from './slices/lobbiesReducer';
import gameReducer from './slices/gameReducer';
import playersReducer from './slices/playersReducer';

export const store = configureStore({
    reducer: {
        lobbies: lobbiesReducer,
        games: gameReducer,
        players: playersReducer
    }
});
