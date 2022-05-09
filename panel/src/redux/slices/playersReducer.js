import { createSlice } from '@reduxjs/toolkit';


const lagSlice = createSlice({
    name: 'players',
    initialState: {
    },
    reducers: {
        reportPings(state, { payload: { players }}) {
            for (const name in players) {
                state[name] = { ...state.player, ping: players[name] }
            }
        },
    }
});

export const { reportPings } = lagSlice.actions;

export default lagSlice.reducer;
