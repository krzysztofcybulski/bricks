import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import { get, post } from '../api';

export const fetchLobbies = createAsyncThunk(
    'lobbies/fetch',
    async () => {
        const lobbies = await get('lobbies');
        const bots = await get('bots');
        return ({ lobbies, bots });
    }
);

export const selectLobby = createAsyncThunk(
    'lobbies/fetchById',
    async ({ id }) => {
        return await get(`lobbies/${id}`);
    }
);

export const addBot = createAsyncThunk(
    'lobbies/addBot',
    async ({ lobbyId, botId }) => {
        await post(`lobbies/${lobbyId}/bots`, {
            name: botId
        });
    }
);

export const createLobby = createAsyncThunk(
    'lobbies/create',
    async () => {
        await post("lobbies");
    }
);

export const startTournament = createAsyncThunk(
    'lobbies/startTournament',
    async ({ lobbyId, settings: { sizes, initTime, moveTime } }, thunkAPI) => {
        await post(`lobbies/${lobbyId}/tournaments`, {
            sizes,
            initTime,
            moveTime
        });
    }
);

const lobbiesSlice = createSlice({
    name: 'lobbies',
    initialState: {
        all: [],
        allBots: [],
        selected: null
    },
    extraReducers: (builder) => {
        builder
            .addCase(selectLobby.fulfilled, (state, action) => {
                state.selected = action.payload;
            })
            .addCase(fetchLobbies.pending, (state) => {
                state.loading = 'pending';
            })
            .addCase(fetchLobbies.fulfilled, (state, { payload: { bots, lobbies } }) => {
                state.loading = 'idle';
                state.all = lobbies;
                state.allBots = bots;
            });
    }
});

export const lobbies = lobbiesSlice;

export default lobbiesSlice.reducer;
