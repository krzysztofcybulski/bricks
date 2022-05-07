import { NotificationManager } from 'react-notifications';
import { Copy } from 'grommet-icons';
import Text from '../atoms/Text';
import HorizontalBox from '../atoms/HorizontalBox';

const CopyComponent = ({ children }) =>
    <HorizontalBox align="center" gap="small" pad="small"
                   border={{ side: 'bottom', color: 'grey', size: '1px' }}>
        <Text size="small">{children}</Text>
        <Copy cursor="pointer"
              size="small"
              onClick={() => {
                  navigator.clipboard.writeText(children);
                  NotificationManager.info('Copied successfully');
              }}/>
    </HorizontalBox>;

export default CopyComponent;
