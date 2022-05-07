import LobbiesList from './fragments/LobbiesList';
import HorizontalBox from './components/atoms/HorizontalBox';
import LobbyDetails from './fragments/LobbyDetails';
import GameViewer from './fragments/GameViewer';
import VerticalBox from './components/atoms/VerticalBox';

const App = () => {
    return (
        <HorizontalBox align="stretch"
                       height="100vh"
                       maxWidth="100vw">
            <VerticalBox style={{ width: 340, zIndex: 1000 }} flex={{ shrink: 0}}>
                <LobbiesList/>
            </VerticalBox>
            <VerticalBox flex={{ grow: 1, shrink: 1 }}>
                <GameViewer/>
            </VerticalBox>
            <VerticalBox style={{ width: 340, zIndex: 1000 }} flex={{ shrink: 0}}>
                <LobbyDetails/>
            </VerticalBox>
        </HorizontalBox>
    );
};

export default App;
