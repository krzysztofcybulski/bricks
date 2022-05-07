import Text from '../../atoms/Text';

const infoToText = ({ currentBlock }) => {
    if (!currentBlock) return '🏗';
    if (currentBlock.move === 0) return 'Initial block';
    if (currentBlock.move > 0) return `Placed by ${currentBlock.player} in ${currentBlock.move} move`;
};

const GameExplainer = ({ currentBlock }) =>
    <Text>
        {infoToText({ currentBlock })}
    </Text>;

export default GameExplainer;

