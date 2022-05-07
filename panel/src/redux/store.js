import { configureStore } from '@reduxjs/toolkit';
import lobbiesReducer from './slices/lobbiesReducer';
import gameReducer from './slices/gameReducer';

export const store = configureStore({
    reducer: {
        lobbies: lobbiesReducer,
        games: gameReducer
    }
});
