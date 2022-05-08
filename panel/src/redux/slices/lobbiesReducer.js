import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import { get, post } from '../api';

export const initData = createAsyncThunk(
    'lobbies/init',
    async () => {
        const lobbies = await get('lobbies');
        const bots = await get('bots');
        return ({ lobbies, bots });
    }
);

export const fetchLobbies = createAsyncThunk(
    'lobbies/fetch',
    async () => {
        return await get('lobbies');
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
    async ({ lobbyId, settings: { sizes, initTime, moveTime } }) => {
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
        selected: null,
        loaded: false
    },
    extraReducers: (builder) => {
        builder
            .addCase(selectLobby.pending, (state, { meta }) => {
                const { arg: { id } } = meta;
                if(state.selected?.id !== id) {
                    state.loadingLobby = true;
                }
            })
            .addCase(selectLobby.fulfilled, (state, action) => {
                state.selected = action.payload;
                state.loadingLobby = false;
            })
            .addCase(initData.fulfilled, (state, { payload: { bots, lobbies } }) => {
                state.loaded = true;
                state.all = lobbies;
                state.allBots = bots;
            })
            .addCase(fetchLobbies.fulfilled, (state, { payload }) => {
                state.all = payload;
            });
    }
});

export const lobbies = lobbiesSlice;

export default lobbiesSlice.reducer;
