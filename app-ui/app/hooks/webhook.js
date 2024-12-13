import crypto from 'crypto';
import axiosConfig from "../config/axiosConfig";

export default async function handler(req, res) {
    if (req.method === 'POST') {
        try {
            const { body, headers } = req;

            // Проверка источника запроса
            const referer = headers['referer'] || headers['origin'];
            if (!referer || !referer.startsWith('https://yoomoney.ru')) {
                return res.status(403).json({ error: 'Invalid request source' });
            }

            // Извлечение параметров из тела запроса
            const {
                notification_type,
                operation_id,
                amount,
                currency,
                datetime,
                sender,
                codepro,
                label,
                sha1_hash
            } = body;

            if (!label || !sha1_hash) {
                return res.status(400).json({ error: 'Missing required parameters: label or sha1_hash' });
            }

            // Проверка подписи (sha1_hash)
            const notificationSecret = "5EG2nUl0OXHIb61XNnAn4POd";
            const hashString = `${notification_type}&${operation_id}&${amount}&${currency}&${datetime}&${sender}&${codepro}&${notificationSecret}&${label}`;
            const calculatedHash = crypto.createHash('sha1').update(hashString).digest('hex');

            if (calculatedHash !== sha1_hash) {
                return res.status(403).json({ error: 'Invalid sha1_hash' });
            }

            // Отправка данных на backend для сохранения статуса платежа
            try {
                const backendResponse = await axiosConfig.get('/payment/save', {
                    params: {
                        label
                    }
                });

                console.log('Backend response:', backendResponse.data);
                return res.status(200).json(backendResponse.data);
            } catch (backendError) {
                console.error('Error from backend:', backendError.response?.data || backendError.message);
                return res.status(500).json({ error: 'Backend error', details: backendError.response?.data });
            }
        } catch (error) {
            console.error('Error handling webhook:', error);
            return res.status(500).json({ error: 'Internal server error' });
        }
    } else {
        res.setHeader('Allow', ['POST']);
        res.status(405).end(`Method ${req.method} not allowed`);
    }
}
