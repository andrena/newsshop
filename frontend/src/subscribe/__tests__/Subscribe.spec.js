// Subscribe.spec.js
import {fireEvent, render, screen} from '@testing-library/vue'
import axios from 'axios';
import Subscribe from '@/subscribe/Subscribe.vue'

jest.mock('axios');

describe('Subscribe.vue', () => {

    test('should be able to add new subscription', async () => {
        axios.post.mockResolvedValueOnce(
            {
                data: {
                    email: 'test@mail.de',
                    name: 'name',
                    source: 'Webseite'
                }
            })

        render(Subscribe)

        expect(screen.getByText('Newsletter Subscription')).toBeInTheDocument();

        expect(screen.getByLabelText('E-Mail:')).toBeInTheDocument();
        await fireEvent.update(screen.getByLabelText('E-Mail:'), "test@mail.de");
        await fireEvent.update(screen.getByLabelText('Name:'), "my name");
        await fireEvent.update(screen.getByLabelText('Quelle:'), "Website");

        await fireEvent.click(screen.getByRole('button', {name: 'Abonnieren'}));

        expect(await screen.findByText('Subscription Successful')).toBeInTheDocument();
    })

})