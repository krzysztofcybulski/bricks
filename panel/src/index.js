import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import 'react-notifications/lib/notifications.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import { grommet, Grommet } from 'grommet';
import { deepMerge } from 'grommet/utils';
import { Provider } from 'react-redux';
import { store } from './redux/store';
import AuthProvider from './utils/AuthProvider';
import MessageDispatcher from './utils/MessageDispatcher';
import Loading from './utils/Loading';

const customTheme = deepMerge(grommet, {
    box: {
        extend: () => `
          &:focus {
            box-shadow: none;
            border-color: initial;
          }
        `
    },
    textInput: {
        extend: () => `
          &:focus {
            box-shadow: none;
            border-color: initial;
          }
        `
    }
});

const root = ReactDOM.createRoot(document.getElementById('root'));

root.render(
    <React.StrictMode>
        <Grommet full theme={customTheme}>
            <Provider store={store}>
                <MessageDispatcher>
                    <AuthProvider>
                        <Loading>
                            <App/>
                        </Loading>
                    </AuthProvider>
                </MessageDispatcher>
            </Provider>
        </Grommet>
    </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
