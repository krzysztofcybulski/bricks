import { connect } from 'react-redux';
import { useAuth0 } from '@auth0/auth0-react';
import VerticalBox from '../components/atoms/VerticalBox';
import Lottie from 'react-lottie';
import loadingAnimation from '../images/cubes_grid';

const defaultOptions = {
    loop: true,
    autoplay: true,
    animationData: loadingAnimation,
    rendererSettings: {
        preserveAspectRatio: "xMidYMid slice"
    }
};

const Loading = ({ lobbiesLoading, children }) => {
    const { isLoading } = useAuth0();
    return isLoading || lobbiesLoading
        ? <VerticalBox justify="center" align="center">
            <Lottie options={defaultOptions} width={400} height={400} />
        </VerticalBox>
        : children;
};

export default connect(
    ({ lobbies: { loaded } }) => ({ lobbiesLoading: !loaded })
)(Loading);
