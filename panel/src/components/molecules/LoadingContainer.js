import React from 'react';
import { Spinner } from 'grommet';
import VerticalBox from '../atoms/VerticalBox';

const LoadingContainer = ({ loadIf, children }) =>
    loadIf
        ? <VerticalBox justify="center" align="center" height="100%"><Spinner/></VerticalBox>
        : children;

export default LoadingContainer;
