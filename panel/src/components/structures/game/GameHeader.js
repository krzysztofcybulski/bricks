import HorizontalBox from '../../atoms/HorizontalBox';
import Avatar from '../../atoms/Avatar';
import Text from '../../atoms/Text';
import { Box, Stack } from 'grommet';
import { Trophy } from 'grommet-icons';
import VerticalBox from "../../atoms/VerticalBox";

const GameHeader = ({ players: { first, second } }) =>
    <HorizontalBox justify="between" align="center" pad="medium">
        <Player player={first}/>
        <Text>vs</Text>
        <Player player={second}/>
    </HorizontalBox>;

const Player = ({ player: { image, color, winner } }) =>
    <VerticalBox align="stretch">
        <Stack align="stretch">
            <Avatar url={image} size="large"/>
            {winner &&
                <Box justify="end" align="center"
                     style={{ position: 'relative', top: 12 }}
                     height="100%" width="100%">
                    <Trophy/>
                </Box>
            }
        </Stack>
        <Box style={{ backgroundColor: color, margin: '12px' }} height='16px'/>
    </VerticalBox>;

export default GameHeader;

