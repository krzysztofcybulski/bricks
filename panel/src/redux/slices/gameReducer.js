import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import { get } from '../api';

export const fetchGame = createAsyncThunk(
    'games/fetch',
    async ({ id }) => {
        return await get(`games/${id}`);
    }
);

const gamesSlice = createSlice({
    name: 'games',
    initialState: {
        game: null,
        gameView: null,
        loading: false,
        currentTime: 0
    },
    extraReducers: (builder) => {
        builder
            .addCase(fetchGame.pending, (state, action) => {
                state.loading = true;
            })
            .addCase(fetchGame.fulfilled, (state, { payload }) => {
                state.loading = false;
                state.game = payload;

                const playerColors = ({
                    [payload.players.first.name]: payload.players.first.color,
                    [payload.players.second.name]: payload.players.second.color
                });

                state.gameView = ({
                    id: state.game.id,
                    players: {
                        first: {
                            ...payload.players.first,
                            winner: payload.winner === payload.players.first.name
                        },
                        second: {
                            ...payload.players.second,
                            winner: payload.winner === payload.players.second.name
                        }
                    },
                    blocks: [
                        ...payload.initialBlocks.map(({ x, y }) => ({ x, y, move: 0, player: "-", color: '#242424' })),
                        ...state.game.moves.flatMap(({ player, brick }, move) =>
                            brick.blocks.map(({ x, y }) => ({ x, y, move: move + 1, player, color: playerColors[player] }))
                        )
                    ],
                    maxTime: payload.moves.length,
                    mapSize: payload.mapSize
                });
            })
    }
});

export default gamesSlice.reducer;
